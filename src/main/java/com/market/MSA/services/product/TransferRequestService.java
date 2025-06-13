package com.market.MSA.services.product;

import com.market.MSA.constants.ProductStatus;
import com.market.MSA.exceptions.AppException;
import com.market.MSA.exceptions.ErrorCode;
import com.market.MSA.mappers.product.TransferRequestMapper;
import com.market.MSA.models.product.Inventory;
import com.market.MSA.models.product.InventoryProduct;
import com.market.MSA.models.product.Transfer;
import com.market.MSA.models.product.TransferItem;
import com.market.MSA.repositories.product.InventoryProductRepository;
import com.market.MSA.repositories.product.InventoryRepository;
import com.market.MSA.repositories.product.TransferRequestRepository;
import com.market.MSA.repositories.user.UserRepository;
import com.market.MSA.requests.product.TransferRequest;
import com.market.MSA.responses.product.TransferResponse;
import com.market.MSA.services.others.EntityFinderService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TransferRequestService {
  final TransferRequestMapper transferRequestMapper;
  final EntityFinderService entityFinderService;
  final InventoryRepository inventoryRepository;
  final UserRepository userRepository;
  final TransferRequestRepository transferRequestRepository;
  final InventoryProductRepository inventoryProductRepository;
  final InventoryProductService inventoryProductService;

  @Transactional
  public TransferResponse createTransferRequest(TransferRequest transferRequest) {
    Transfer transfer = transferRequestMapper.toTransferRequest(transferRequest);
    transfer.setFromInventory(
        entityFinderService.findByIdOrThrow(
            inventoryRepository, 1L, ErrorCode.INVENTORY_NOT_FOUND));
    transfer.setToInventory(
        entityFinderService.findByIdOrThrow(
            inventoryRepository,
            transferRequest.getToInventoryId(),
            ErrorCode.INVENTORY_NOT_FOUND));
    transfer.setRequester(
        entityFinderService.findByIdOrThrow(
            userRepository, transferRequest.getRequesterId(), ErrorCode.USER_NOT_EXISTED));
    transfer.setApprover(
        entityFinderService.findByIdOrThrow(userRepository, 1L, ErrorCode.USER_NOT_EXISTED));
    transfer.setCreatedAt(LocalDateTime.now());

    Transfer saveTransfer = transferRequestRepository.save(transfer);

    return transferRequestMapper.toTransferResponse(saveTransfer);
  }

  @Transactional
  public TransferResponse updateTransferRequest(Long transferId, TransferRequest transferRequest) {
    Transfer transfer =
        transferRequestRepository
            .findById(transferId)
            .orElseThrow(() -> new AppException(ErrorCode.TRANSFER_REQUEST_NOT_FOUND));
    transfer.setFromInventory(
        entityFinderService.findByIdOrThrow(
            inventoryRepository,
            transferRequest.getFromInventoryId(),
            ErrorCode.INVENTORY_NOT_FOUND));
    transfer.setToInventory(
        entityFinderService.findByIdOrThrow(
            inventoryRepository,
            transferRequest.getToInventoryId(),
            ErrorCode.INVENTORY_NOT_FOUND));
    transfer.setRequester(
        entityFinderService.findByIdOrThrow(
            userRepository, transferRequest.getRequesterId(), ErrorCode.USER_NOT_EXISTED));
    transfer.setApprover(
        entityFinderService.findByIdOrThrow(
            userRepository, transferRequest.getApproverId(), ErrorCode.USER_NOT_EXISTED));

    transferRequestMapper.updateTransferRequest(transferRequest, transfer);
    Transfer updatedTransfer = transferRequestRepository.save(transfer);
    return transferRequestMapper.toTransferResponse(updatedTransfer);
  }

  public boolean deleteTransferRequest(Long transferId) {
    if (!transferRequestRepository.existsById(transferId)) {
      throw new AppException(ErrorCode.TRANSFER_REQUEST_NOT_FOUND);
    }
    transferRequestRepository.deleteById(transferId);
    return true;
  }

  public TransferResponse getTransferRequestById(Long transferId) {
    return transferRequestMapper.toTransferResponse(
        transferRequestRepository
            .findById(transferId)
            .orElseThrow(() -> new AppException(ErrorCode.TRANSFER_REQUEST_NOT_FOUND)));
  }

  public List<TransferResponse> getAllTransferRequests(int page, int pageSize) {
    return transferRequestRepository.findAll().stream()
        .skip((long) (page - 1) * pageSize)
        .limit(pageSize)
        .map(transferRequestMapper::toTransferResponse)
        .collect(Collectors.toList());
  }

  public List<TransferResponse> getTransferRequestsByRequesterId(
      Long requesterId, int page, int pageSize) {
    Pageable pageable = PageRequest.of(page, pageSize);
    return transferRequestRepository
        .findByUser_RequesterIdWithPageable(requesterId, pageable)
        .stream()
        .map(transferRequestMapper::toTransferResponse)
        .collect(Collectors.toList());
  }

  public List<TransferResponse> getTransferRequestsByApproverId(
      Long approverId, int page, int pageSize) {
    Pageable pageable = PageRequest.of(page, pageSize);
    return transferRequestRepository
        .findByUser_ApproverIdWithPageable(approverId, pageable)
        .stream()
        .map(transferRequestMapper::toTransferResponse)
        .collect(Collectors.toList());
  }

  public List<TransferResponse> getTransferRequestsByFromInventoryId(
      Long fromInventoryId, int page, int pageSize) {
    Pageable pageable = PageRequest.of(page, pageSize);
    return transferRequestRepository
        .findByInventory_FromInventoryIdWithPageable(fromInventoryId, pageable)
        .stream()
        .map(transferRequestMapper::toTransferResponse)
        .collect(Collectors.toList());
  }

  public List<TransferResponse> getTransferRequestsByToInventoryId(
      Long toInventoryId, int page, int pageSize) {
    Pageable pageable = PageRequest.of(page, pageSize);
    return transferRequestRepository
        .findByInventory_ToInventoryIdWithPageable(toInventoryId, pageable)
        .stream()
        .map(transferRequestMapper::toTransferResponse)
        .collect(Collectors.toList());
  }

  @Transactional
  public TransferResponse approveTransferRequest(Long transferId) {
    Transfer transfer =
        transferRequestRepository
            .findById(transferId)
            .orElseThrow(() -> new AppException(ErrorCode.TRANSFER_REQUEST_NOT_FOUND));

    // Check if request is already processed
    if (!ProductStatus.PENDING.getValue().equals(transfer.getStatus())) {
      throw new AppException(ErrorCode.TRANSFER_REQUEST_ALREADY_PROCESSED);
    }

    // Get central inventory (branch 1)
    Inventory centralInventory =
        inventoryRepository
            .findByBranch_BranchId(1L)
            .orElseThrow(() -> new AppException(ErrorCode.INVENTORY_NOT_FOUND));

    // Process each transfer item
    for (TransferItem item : transfer.getTransferItems()) {
      // Check if product exists in central inventory
      List<InventoryProduct> centralInventoryProducts =
          inventoryProductRepository.findByInventory_InventoryIdAndProduct_ProductId(
              centralInventory.getInventoryId(), item.getProduct().getProductId());

      if (centralInventoryProducts.isEmpty()) {
        throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);
      }

      // Get the source inventory product (first one with available stock)
      InventoryProduct centralInventoryProduct =
          centralInventoryProducts.stream()
              .filter(ip -> ip.getStockNumber() > 0)
              .findFirst()
              .orElse(centralInventoryProducts.getFirst());

      // Get the expDate from the source inventory product
      LocalDateTime sourceExpDate = centralInventoryProduct.getExpDate();

      // Check if we should use this inventory product or create a new one
      if (centralInventoryProduct.getStockNumber() == 0) {
        // If stock is zero, we can reuse this inventory product
        // No need to update expDate as it's already set
      } else if (sourceExpDate != null) {
        // If there's an expDate, check if we need to create a new inventory product
        // Find if there's already an inventory product with the same expDate and zero stock
        Optional<InventoryProduct> existingZeroStock =
            centralInventoryProducts.stream()
                .filter(ip -> ip.getStockNumber() == 0 && sourceExpDate.equals(ip.getExpDate()))
                .findFirst();

        if (existingZeroStock.isPresent()) {
          // Use the existing zero-stock item with matching expDate
          centralInventoryProduct = existingZeroStock.get();
        } else if (centralInventoryProducts.stream()
                .filter(ip -> sourceExpDate.equals(ip.getExpDate()) && ip.getStockNumber() > 0)
                .count()
            > 1) {
          // If there are multiple inventory products with the same expDate and positive stock,
          // create a new one to maintain batch separation
          InventoryProduct newProduct =
              InventoryProduct.builder()
                  .inventory(centralInventory)
                  .product(item.getProduct())
                  .expDate(sourceExpDate)
                  .stockNumber(0)
                  .currentPrice(centralInventoryProduct.getCurrentPrice())
                  .isActive(true)
                  .isDiscounted(centralInventoryProduct.isDiscounted())
                  .build();

          // Save the new product
          centralInventoryProduct = inventoryProductRepository.save(newProduct);
        }
      }

      // Check if central inventory has enough stock
      if (centralInventoryProduct.getStockNumber() < item.getQuantityRequested()) {
        throw new AppException(ErrorCode.INSUFFICIENT_STOCK);
      }

      // Deduct stock from central inventory
      centralInventoryProduct.setStockNumber(
          centralInventoryProduct.getStockNumber() - item.getQuantityRequested());
      inventoryProductRepository.save(centralInventoryProduct);

      // Add stock to destination inventory
      InventoryProduct finalCentralInventoryProduct = centralInventoryProduct;
      InventoryProduct destinationInventoryProduct =
          inventoryProductRepository
              .findByInventory_InventoryIdAndProduct_ProductId(
                  transfer.getToInventory().getInventoryId(), item.getProduct().getProductId())
              .stream()
              .filter(
                  ip ->
                      sourceExpDate == null
                          ? ip.getExpDate() == null
                          : (ip.getExpDate() != null && ip.getExpDate().equals(sourceExpDate)))
              .findFirst()
              .orElseGet(
                  () -> {
                    InventoryProduct newProduct =
                        InventoryProduct.builder()
                            .inventory(transfer.getToInventory())
                            .product(item.getProduct())
                            .expDate(sourceExpDate)
                            .stockNumber(0)
                            .currentPrice(finalCentralInventoryProduct.getCurrentPrice())
                            .isActive(true)
                            .isDiscounted(finalCentralInventoryProduct.isDiscounted())
                            .build();
                    return inventoryProductRepository.save(newProduct);
                  });

      destinationInventoryProduct.setStockNumber(
          destinationInventoryProduct.getStockNumber() + item.getQuantityTransferred());
      inventoryProductRepository.save(destinationInventoryProduct);

      // Update transferred quantity
      item.setQuantityTransferred(item.getQuantityTransferred());

      inventoryProductService.updateStockLevel(centralInventoryProduct);
      inventoryProductService.updateStockLevel(destinationInventoryProduct);
    }

    // Update transfer request status
    transfer.setStatus(ProductStatus.APPROVED.getValue());
    transfer.setUpdatedAt(LocalDateTime.now());
    Transfer updatedTransfer = transferRequestRepository.save(transfer);

    return transferRequestMapper.toTransferResponse(updatedTransfer);
  }

  @Transactional
  public TransferResponse rejectTransferRequest(Long transferId, String note) {
    Transfer transfer =
        transferRequestRepository
            .findById(transferId)
            .orElseThrow(() -> new AppException(ErrorCode.TRANSFER_REQUEST_NOT_FOUND));

    // Check if request is already processed
    if (!ProductStatus.PENDING.getValue().equals(transfer.getStatus())) {
      throw new AppException(ErrorCode.TRANSFER_REQUEST_ALREADY_PROCESSED);
    }

    // Update transfer request status
    transfer.setStatus(ProductStatus.REJECTED.getValue());
    transfer.setNote(note);
    transfer.setUpdatedAt(LocalDateTime.now());
    Transfer updatedTransfer = transferRequestRepository.save(transfer);

    return transferRequestMapper.toTransferResponse(updatedTransfer);
  }
}
