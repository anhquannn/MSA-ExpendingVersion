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
    // 1. Xác định kho HEAD (có branchId = 1). Mặc định hệ thống luôn có.
    Inventory headInventory =
        inventoryRepository
            .findByBranch_BranchId(1L)
            .orElseThrow(() -> new IllegalStateException("HEAD inventory (branchId=1) not found"));

    // 2. Lấy danh sách tồn kho thấp hơn ngưỡng tối thiểu.
    List<InventoryProduct> allInventoryProducts =
        inventoryProductRepository.filter(
            null, null, null, true, null, null, null, Sort.unsorted());

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
      return new ArrayList<>();
    }

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

    int totalTransfersProcessed = 0;
    int totalItemsCreated = 0;
    List<Transfer> createdTransfers = new ArrayList<>();

    for (Map.Entry<Inventory, List<InventoryProduct>> entry : groupedByInventory.entrySet()) {
      Inventory destInv = entry.getKey();
      List<InventoryProduct> productsNeedingTransfer = entry.getValue();

      // 4. Tìm manager của kho đích làm requester
      User requesterUser = destInv.getBranch().getUsers().stream().findFirst().orElse(null);

      if (requesterUser == null) {
        continue;
      }

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

        // 7. Kiểm tra xem sản phẩm này đã có trong transfer chưa
        if (existingProductIds.contains(productId)) {
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
          continue;
        }

        // 9. Tính toán số lượng cần request
        int requestQty = Math.max(0, (ip.getMaxThreshold() / 2) - ip.getStockNumber());

        if (requestQty == 0) {
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

        if (headStock < requestQty) {
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
          } catch (Exception ignored) {
          }
        }
      } else {
        log.info("No new items to add for destination inventory: {}", destInv.getInventoryId());
      }
    }

    return createdTransfers;
  }
}
