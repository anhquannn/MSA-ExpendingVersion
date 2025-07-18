package com.market.MSA.services.product;

import com.market.MSA.constants.ProductStatus;
import com.market.MSA.exceptions.AppException;
import com.market.MSA.exceptions.ErrorCode;
import com.market.MSA.mappers.product.TransferRequestMapper;
import com.market.MSA.models.product.*;
import com.market.MSA.repositories.product.InboundRepository;
import com.market.MSA.repositories.product.InventoryProductRepository;
import com.market.MSA.repositories.product.InventoryRepository;
import com.market.MSA.repositories.product.OutboundRepository;
import com.market.MSA.repositories.product.TransferRequestRepository;
import com.market.MSA.repositories.user.UserRepository;
import com.market.MSA.requests.filters.TransferRequestFilterRequest;
import com.market.MSA.requests.product.TransferRequest;
import com.market.MSA.responses.product.TransferResponse;
import com.market.MSA.services.others.EntityFinderService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
  final OutboundRepository outboundRepository;
  final InboundRepository inboundRepository;

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
    transfer.setStatus(ProductStatus.PENDING);
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
    transfer.setUpdatedAt(LocalDateTime.now());

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

  // @Cacheable("all_transfer_requests")
  public List<TransferResponse> getAll() {
    return transferRequestRepository.findAll().stream()
        .map(transferRequestMapper::toTransferResponse)
        .collect(Collectors.toList());
  }

  // @Cacheable("transfer_requests_list")
  public List<TransferResponse> getAllTransferRequests(TransferRequestFilterRequest request) {
    Sort sort = Sort.by(Sort.Direction.fromString(request.getSortDirection()), request.getSortBy());

    return transferRequestRepository
        .filter(
            request.getRequesterId(),
            request.getApproverId(),
            request.getFromInventoryId(),
            request.getToInventoryId(),
            request.getStatus(),
            request.getFromDate(),
            request.getToDate(),
            sort)
        .stream()
        .map(transferRequestMapper::toTransferResponse)
        .collect(Collectors.toList());
  }

  // @Cacheable("transfer_requests_paging")
  public Page<TransferResponse> getAllTransferRequestsWithPaging(
      TransferRequestFilterRequest request) {
    Sort sort = Sort.by(Sort.Direction.fromString(request.getSortDirection()), request.getSortBy());

    Pageable pageable = PageRequest.of(request.getPage() - 1, request.getPageSize(), sort);

    return transferRequestRepository
        .filterWithPaging(
            request.getRequesterId(),
            request.getApproverId(),
            request.getFromInventoryId(),
            request.getToInventoryId(),
            request.getStatus(),
            request.getFromDate(),
            request.getToDate(),
            pageable)
        .map(transferRequestMapper::toTransferResponse);
  }

  @Transactional
  public TransferResponse approveTransferRequest(Long transferId) {
    // 1. Tìm yêu cầu chuyển kho.
    Transfer transfer =
        transferRequestRepository
            .findById(transferId)
            .orElseThrow(() -> new AppException(ErrorCode.TRANSFER_REQUEST_NOT_FOUND));

    // 2. Chỉ duyệt được các yêu cầu đang ở trạng thái "Chờ xử lý" (PENDING).
    if (!ProductStatus.PENDING.equals(transfer.getStatus())) {
      throw new AppException(ErrorCode.TRANSFER_REQUEST_ALREADY_PROCESSED);
    }

    // 3. Lấy thông tin kho nguồn (thường là kho trung tâm) và kho đích.
    Inventory centralInventory =
        inventoryRepository
            .findByBranch_BranchId(1L)
            .orElseThrow(() -> new AppException(ErrorCode.INVENTORY_NOT_FOUND));
    Inventory destinationInventory = transfer.getToInventory();

    // 4. Duyệt qua từng sản phẩm trong yêu cầu.
    for (TransferItem item : transfer.getTransferItems()) {
      // 4.1. Tìm sản phẩm trong kho nguồn.
      List<InventoryProduct> centralInventoryProducts =
          inventoryProductRepository.filter(
              item.getProduct().getProductId(),
              centralInventory.getInventoryId(),
              null,
              null,
              null,
              null,
              null,
              null);
      if (centralInventoryProducts.isEmpty()) throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);

      // 4.2. Tìm sản phẩm còn hàng đầu tiên để xuất.
      InventoryProduct centralInventoryProduct =
          centralInventoryProducts.stream()
              .filter(ip -> ip.getStockNumber() > 0)
              .findFirst()
              .orElse(centralInventoryProducts.getFirst());

      // 4.3. Lấy thông tin lô và HSD của lô hàng sẽ xuất đi.
      LocalDateTime sourceExpDate = centralInventoryProduct.getExpDate();
      String sourceBatchNumber = centralInventoryProduct.getBatchNumber();

      // 4.4. Chuẩn bị bản ghi sản phẩm tại kho đích.
      if (!destinationInventory.getInventoryId().equals(centralInventory.getInventoryId())) {
        List<InventoryProduct> destInventoryProducts =
            inventoryProductRepository.filter(
                item.getProduct().getProductId(),
                destinationInventory.getInventoryId(),
                null,
                null,
                null,
                null,
                null,
                null);

        if (destInventoryProducts.isEmpty()) {
          // Nếu sản phẩm chưa từng có ở kho đích, tạo một bản ghi mới với số lượng = 0,
          // sẵn sàng để nhận hàng. Lô và HSD sẽ giống kho nguồn.
          inventoryProductRepository.save(
              InventoryProduct.builder()
                  .inventory(destinationInventory)
                  .product(item.getProduct())
                  .expDate(sourceExpDate)
                  .batchNumber(sourceBatchNumber)
                  .stockNumber(0)
                  .currentPrice(centralInventoryProduct.getCurrentPrice())
                  .isActive(true)
                  .isDiscounted(centralInventoryProduct.isDiscounted())
                  .build());
        } else {
          // Nếu đã có, kiểm tra xem có thể nhận hàng vào lô hiện tại không.
          InventoryProduct destInventoryProduct = destInventoryProducts.getFirst();
          boolean batchDiff =
              (sourceBatchNumber != null
                      && !sourceBatchNumber.equals(destInventoryProduct.getBatchNumber()))
                  || (sourceBatchNumber == null && destInventoryProduct.getBatchNumber() != null);
          boolean expDiff =
              (sourceExpDate != null && !sourceExpDate.equals(destInventoryProduct.getExpDate()))
                  || (sourceExpDate == null && destInventoryProduct.getExpDate() != null);
          if (destInventoryProduct.getStockNumber() > 0 && (batchDiff || expDiff)) {
            // Nếu kho đích đang có hàng của một lô khác, không cho phép trộn lẫn.
            throw new AppException(ErrorCode.INVALID_BATCH_OR_EXPDATE);
          }
        }
      }

      // 4.5. Kiểm tra xem kho nguồn có đủ hàng không.
      if (centralInventoryProduct.getStockNumber() < item.getQuantityRequested()) {
        throw new AppException(ErrorCode.INSUFFICIENT_STOCK);
      }

      // 4.6. TRỪ TỒN KHO tại kho nguồn.
      centralInventoryProduct.setStockNumber(
          centralInventoryProduct.getStockNumber() - item.getQuantityRequested());
      inventoryProductRepository.save(centralInventoryProduct);
      inventoryProductService.updateStockLevel(centralInventoryProduct);

      // CHÚ Ý: Tồn kho tại kho đích CHƯA được cộng. Việc này sẽ được thực hiện khi kho đích xác
      // nhận nhận hàng.

      // 4.7. Ghi nhận số lượng đã thực sự chuyển.
      item.setQuantityTransferred(item.getQuantityRequested());
    }

    // 5. Cập nhật trạng thái yêu cầu thành "Đã duyệt" (APPROVED).
    transfer.setStatus(ProductStatus.APPROVED);
    transfer.setUpdatedAt(LocalDateTime.now());
    Transfer updatedTransfer = transferRequestRepository.save(transfer);

    // 6. Tạo bản ghi xuất kho (Outbound) cho kho nguồn.
    outboundRepository.save(
        OutboundTransfer.builder()
            .status(ProductStatus.SHIPPED)
            .outboundTransferDate(LocalDateTime.now())
            .inventory(updatedTransfer.getFromInventory())
            .user(updatedTransfer.getApprover())
            .transfer(updatedTransfer)
            .build());

    // 7. Tạo bản ghi nhận kho (Inbound) cho kho đích, ở trạng thái chờ nhận.
    inboundRepository.save(
        InboundTransfer.builder()
            .status(ProductStatus.IN_PROGRESS)
            .inboundTransferDate(LocalDateTime.now())
            .inventory(updatedTransfer.getToInventory())
            .user(updatedTransfer.getRequester())
            .transfer(updatedTransfer)
            .build());

    return transferRequestMapper.toTransferResponse(updatedTransfer);
  }

  @Transactional
  public TransferResponse rejectTransferRequest(Long transferId, String note) {
    Transfer transfer =
        transferRequestRepository
            .findById(transferId)
            .orElseThrow(() -> new AppException(ErrorCode.TRANSFER_REQUEST_NOT_FOUND));

    // Chỉ từ chối được các yêu cầu đang chờ xử lý.
    if (!ProductStatus.PENDING.equals(transfer.getStatus())) {
      throw new AppException(ErrorCode.TRANSFER_REQUEST_ALREADY_PROCESSED);
    }

    // Cập nhật trạng thái thành "Đã từ chối" (REJECTED) và ghi lại lý do.
    transfer.setStatus(ProductStatus.REJECTED);
    transfer.setNote(note);
    transfer.setUpdatedAt(LocalDateTime.now());
    Transfer updatedTransfer = transferRequestRepository.save(transfer);

    return transferRequestMapper.toTransferResponse(updatedTransfer);
  }
}
