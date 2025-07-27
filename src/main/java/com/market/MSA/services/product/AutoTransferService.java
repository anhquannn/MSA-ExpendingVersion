package com.market.MSA.services.product;

import com.market.MSA.constants.ProductStatus;
import com.market.MSA.models.product.Inventory;
import com.market.MSA.models.product.InventoryProduct;
import com.market.MSA.models.product.Transfer;
import com.market.MSA.models.product.TransferItem;
import com.market.MSA.models.user.User;
import com.market.MSA.repositories.product.InventoryProductRepository;
import com.market.MSA.repositories.product.InventoryRepository;
import com.market.MSA.repositories.product.TransferRequestRepository;
import com.market.MSA.repositories.user.UserRepository;
import com.market.MSA.services.others.NotificationService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Service that scans inventory and auto-creates transfer requests when stock is low. */
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AutoTransferService {

  InventoryProductRepository inventoryProductRepository;
  TransferRequestRepository transferRequestRepository;
  InventoryRepository inventoryRepository;
  UserRepository userRepository;
  NotificationService notificationService;

  // Check kỹ tồn kho có trong HEAD rồi mới tạo TransferRequest.
  @Transactional(rollbackFor = Exception.class)
  public List<Transfer> processLowStock() {
    log.info("Starting auto transfer process...");

    // 1. Xác định kho HEAD (có branchId = 1). Mặc định hệ thống luôn có.
    Inventory headInventory =
        inventoryRepository
            .findByBranch_BranchId(1L)
            .orElseThrow(() -> new IllegalStateException("HEAD inventory (branchId=1) not found"));

    log.info("Found HEAD inventory: {}", headInventory.getInventoryId());

    // 2. Lấy danh sách tồn kho thấp hơn ngưỡng tối thiểu.
    List<InventoryProduct> allInventoryProducts =
        inventoryProductRepository.filter(
            null, null, null, true, null, null, null, Sort.unsorted());

    log.info("Total active inventory products: {}", allInventoryProducts.size());

    List<InventoryProduct> lowStocks =
        allInventoryProducts.stream()
            .filter(
                ip -> {
                  boolean isLowStock = ip.getStockNumber() < ip.getMinThreshold();
                  if (isLowStock) {
                    log.info(
                        "Low stock found - Product: {}, Current: {}, Min: {}, Inventory: {}",
                        ip.getProduct().getProductId(),
                        ip.getStockNumber(),
                        ip.getMinThreshold(),
                        ip.getInventory().getInventoryId());
                  }
                  return isLowStock;
                })
            .toList();

    if (lowStocks.isEmpty()) {
      log.info("No low stock items found");
      return new ArrayList<>();
    }
    log.info("Found {} low stock items", lowStocks.size());

    // 3. Gom nhóm theo kho đích để tạo 1 yêu cầu/vận chuyển/kho.
    Map<Inventory, List<InventoryProduct>> groupedByInventory =
        lowStocks.stream()
            .filter(
                ip -> {
                  boolean notHeadInventory =
                      !ip.getInventory().getInventoryId().equals(headInventory.getInventoryId());
                  if (!notHeadInventory) {
                    log.info("Skipping HEAD inventory product: {}", ip.getProduct().getProductId());
                  }
                  return notHeadInventory;
                })
            .collect(Collectors.groupingBy(InventoryProduct::getInventory));

    log.info("Grouped into {} destination inventories", groupedByInventory.size());

    int totalTransfersProcessed = 0;
    int totalItemsCreated = 0;
    List<Transfer> createdTransfers = new ArrayList<>();

    for (Map.Entry<Inventory, List<InventoryProduct>> entry : groupedByInventory.entrySet()) {
      Inventory destInv = entry.getKey();
      List<InventoryProduct> productsNeedingTransfer = entry.getValue();
      log.info(
          "Processing destination inventory ID: {} with {} products",
          destInv.getInventoryId(),
          productsNeedingTransfer.size());

      // 4. Tìm manager của kho đích làm requester
      User requesterUser = destInv.getBranch().getUsers().stream().findFirst().orElse(null);

      if (requesterUser == null) {
        log.warn("No manager found for inventory ID: {}, skipping", destInv.getInventoryId());
        continue;
      }
      log.info(
          "Using requester user ID: {} for inventory: {}",
          requesterUser.getUserId(),
          destInv.getInventoryId());

      // 5. Kiểm tra xem đã có transfer PENDING nào cho kho này chưa
      List<Transfer> existingPendingTransfers =
          transferRequestRepository.filter(
              null,
              null,
              headInventory.getInventoryId(),
              destInv.getInventoryId(),
              ProductStatus.PENDING,
              null,
              null,
              Sort.unsorted());

      Transfer transfer;
      boolean isNewTransfer = false;

      if (!existingPendingTransfers.isEmpty()) {
        // Sử dụng transfer PENDING đã có
        transfer = existingPendingTransfers.getFirst();
        log.info(
            "Found existing PENDING transfer ID: {} for destination inventory: {}",
            transfer.getTransferRequestId(),
            destInv.getInventoryId());
      } else {
        // Tạo transfer mới
        transfer = new Transfer();
        transfer.setFromInventory(headInventory);
        transfer.setToInventory(destInv);
        transfer.setRequester(requesterUser);
        transfer.setApprover(
            userRepository
                .findById(1L)
                .orElse(requesterUser)); // fallback to requester if system user not found
        transfer.setStatus(ProductStatus.PENDING);
        transfer.setCreatedAt(LocalDateTime.now());
        transfer.setTransferItems(new ArrayList<>());
        isNewTransfer = true;
        log.info(
            "Created new transfer from inventory ID: {} to inventory ID: {}",
            headInventory.getInventoryId(),
            destInv.getInventoryId());
      }

      // 6. Lấy danh sách sản phẩm đã có trong transfer này (để tránh trùng lặp)
      Set<Long> existingProductIds =
          transfer.getTransferItems().stream()
              .map(item -> item.getProduct().getProductId())
              .collect(Collectors.toSet());

      List<TransferItem> transferItems = transfer.getTransferItems();
      int itemsAddedForThisTransfer = 0;

      for (InventoryProduct ip : productsNeedingTransfer) {
        Long productId = ip.getProduct().getProductId();
        log.debug(
            "Processing product: {} in inventory: {}",
            productId,
            ip.getInventory().getInventoryId());

        // 7. Kiểm tra xem sản phẩm này đã có trong transfer chưa
        if (existingProductIds.contains(productId)) {
          log.info("Product {} already exists in transfer, skipping", productId);
          continue;
        }

        // 8. Kiểm tra xem có transfer ACTIVE khác (APPROVED/IN_PROGRESS) cho sản phẩm này không
        boolean hasActiveTransferForProduct =
            transferRequestRepository
                .filter(
                    null,
                    null,
                    headInventory.getInventoryId(),
                    destInv.getInventoryId(),
                    null,
                    null,
                    null,
                    Sort.unsorted())
                .stream()
                .filter(
                    t ->
                        t.getStatus() == ProductStatus.APPROVED
                            || t.getStatus() == ProductStatus.IN_PROGRESS)
                .flatMap(t -> t.getTransferItems().stream())
                .anyMatch(item -> item.getProduct().getProductId().equals(productId));

        if (hasActiveTransferForProduct) {
          log.info("Product {} has active transfer in progress, skipping", productId);
          continue;
        }

        // 9. Tính toán số lượng cần request
        int requestQty = Math.max(0, (ip.getMaxThreshold() / 2) - ip.getStockNumber());
        log.info(
            "Product: {}, MaxThreshold: {}, CurrentStock: {}, RequestQty: {}",
            productId,
            ip.getMaxThreshold(),
            ip.getStockNumber(),
            requestQty);

        if (requestQty == 0) {
          log.info("Request quantity <= 0 for product: {}, skipping", productId);
          continue;
        }

        // 10. Kiểm tra kho HEAD còn đủ hàng để chuyển
        List<InventoryProduct> headProducts =
            inventoryProductRepository.filter(
                productId,
                headInventory.getInventoryId(),
                null,
                true,
                null,
                null,
                null,
                Sort.unsorted());

        int headStock = headProducts.stream().mapToInt(InventoryProduct::getStockNumber).sum();

        log.info("HEAD stock for product {}: {}, Required: {}", productId, headStock, requestQty);

        if (headStock < requestQty) {
          log.warn(
              "Insufficient HEAD stock for product: {}, Available: {}, Required: {}",
              productId,
              headStock,
              requestQty);
          continue;
        }

        // 11. Tạo TransferItem mới
        TransferItem item = new TransferItem();
        item.setTransfer(transfer);
        item.setProduct(ip.getProduct());
        item.setQuantityRequested(requestQty);
        item.setQuantityTransferred(requestQty);
        transferItems.add(item);
        existingProductIds.add(productId); // Thêm vào set để tránh trùng lặp trong cùng transfer
        itemsAddedForThisTransfer++;

        log.info("Added transfer item - Product: {}, Quantity: {}", productId, requestQty);
      }

      // 12. Lưu transfer nếu có items mới hoặc có thay đổi
      if (itemsAddedForThisTransfer > 0 || isNewTransfer) {
        transfer.setUpdatedAt(LocalDateTime.now());

        Transfer savedTransfer = transferRequestRepository.save(transfer);
        createdTransfers.add(savedTransfer);
        totalTransfersProcessed++;
        totalItemsCreated += itemsAddedForThisTransfer;

        // Send notification for new transfer
        if (isNewTransfer) {
          try {
            notificationService.sendAutoTransferCreatedNotification(savedTransfer);
            log.info(
                "✅ Sent auto transfer notification for transfer ID: {}",
                savedTransfer.getTransferRequestId());
          } catch (Exception e) {
            log.error(
                "❌ Failed to send auto transfer notification for transfer ID: {}",
                savedTransfer.getTransferRequestId(),
                e);
          }
        }

        log.info(
            "Saved transfer ID: {} with {} total items ({} new items added)",
            savedTransfer.getTransferRequestId(),
            savedTransfer.getTransferItems().size(),
            itemsAddedForThisTransfer);
      } else {
        log.info("No new items to add for destination inventory: {}", destInv.getInventoryId());
      }
    }

    log.info(
        "Auto transfer process completed - Processed {} transfers with {} total new items",
        totalTransfersProcessed,
        totalItemsCreated);

    return createdTransfers;
  }
}
