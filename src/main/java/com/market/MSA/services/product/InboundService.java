package com.market.MSA.services.product;

import com.market.MSA.constants.ProductStatus;
import com.market.MSA.exceptions.AppException;
import com.market.MSA.exceptions.ErrorCode;
import com.market.MSA.mappers.product.InboundMapper;
import com.market.MSA.models.product.InboundTransfer;
import com.market.MSA.models.product.InventoryProduct;
import com.market.MSA.models.product.Transfer;
import com.market.MSA.models.product.TransferItem;
import com.market.MSA.repositories.product.InboundRepository;
import com.market.MSA.repositories.product.InventoryProductRepository;
import com.market.MSA.requests.filters.InboundFilterRequest;
import com.market.MSA.requests.product.InboundRequest;
import com.market.MSA.responses.product.InboundResponse;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InboundService {
  InboundRepository inboundTransferRepository;
  InboundMapper inboundMapper;
  InventoryProductRepository inventoryProductRepository;
  InventoryProductService inventoryProductService;

  @Transactional
  public InboundResponse createInboundTransfer(InboundRequest request) {
    InboundTransfer inboundTransfer = inboundMapper.toInbound(request);
    inboundTransfer = inboundTransferRepository.save(inboundTransfer);
    return inboundMapper.toInboundResponse(inboundTransfer);
  }

  @Transactional
  public InboundResponse updateInboundTransfer(Long inboundTransferId, InboundRequest request) {
    InboundTransfer inboundTransfer =
        inboundTransferRepository
            .findById(inboundTransferId)
            .orElseThrow(() -> new AppException(ErrorCode.INBOUND_TRANSFER_NOT_FOUND));

    // Cờ này kiểm tra xem có phải là thao tác "Nhận hàng" hay không
    // (tức là chuyển trạng thái từ "Đang vận chuyển" sang "Đã nhận").
    boolean isReceiving =
        ProductStatus.IN_PROGRESS.equals(inboundTransfer.getStatus())
            && ProductStatus.RECEIVED.equals(request.getStatus());

    // Cập nhật thông tin chung của yêu cầu nhận hàng.
    inboundMapper.updateInbound(request, inboundTransfer);
    inboundTransfer = inboundTransferRepository.save(inboundTransfer);

    // CHỈ CẬP NHẬT TỒN KHO KHI THỰC SỰ NHẬN HÀNG.
    if (isReceiving) {
      Transfer transfer = inboundTransfer.getTransfer();

      // Duyệt qua từng sản phẩm trong phiếu chuyển kho.
      for (TransferItem item : transfer.getTransferItems()) {
        // Tìm sản phẩm trong kho nguồn để lấy thông tin lô hàng (batch number).
        List<InventoryProduct> centralInvProducts =
            inventoryProductRepository.filter(
                item.getProduct().getProductId(),
                transfer.getFromInventory().getInventoryId(),
                null,
                null,
                null,
                null,
                null,
                null);

        if (centralInvProducts.isEmpty()) {
          throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);
        }
        InventoryProduct sourceInvProduct = centralInvProducts.getFirst();

        // Tìm sản phẩm trong kho đích dựa trên lô hàng của kho nguồn.
        List<InventoryProduct> destProducts =
            inventoryProductRepository.filter(
                item.getProduct().getProductId(),
                inboundTransfer.getInventory().getInventoryId(),
                sourceInvProduct.getBatchNumber(), // Đảm bảo nhận đúng lô hàng
                null,
                null,
                null,
                null,
                null);

        if (destProducts.isEmpty()) {
          throw new AppException(ErrorCode.INVALID_BATCH_OR_EXPDATE);
        }

        // Cộng số lượng đã chuyển vào tồn kho của sản phẩm tại kho đích.
        InventoryProduct destProduct = destProducts.getFirst();
        destProduct.setStockNumber(destProduct.getStockNumber() + item.getQuantityTransferred());
        // Cập nhật lại trạng thái mức tồn kho (ví dụ: LOW_STOCK, IN_STOCK).
        inventoryProductService.updateStockLevel(destProduct);
        inventoryProductRepository.save(destProduct);
      }
    }

    return inboundMapper.toInboundResponse(inboundTransfer);
  }

  @Transactional
  public boolean deleteInboundTransfer(Long inboundTransferId) {
    if (!inboundTransferRepository.existsById(inboundTransferId)) {
      throw new AppException(ErrorCode.INBOUND_TRANSFER_NOT_FOUND);
    }
    inboundTransferRepository.deleteById(inboundTransferId);
    return true;
  }

  public InboundResponse getInboundTransferById(Long inboundTransferId) {
    InboundTransfer inboundTransfer =
        inboundTransferRepository
            .findById(inboundTransferId)
            .orElseThrow(() -> new AppException(ErrorCode.INBOUND_TRANSFER_NOT_FOUND));
    return inboundMapper.toInboundResponse(inboundTransfer);
  }

  @Cacheable("all_inbound_transfers")
  public List<InboundResponse> getAll() {
    return inboundTransferRepository.findAll().stream()
        .map(inboundMapper::toInboundResponse)
        .collect(Collectors.toList());
  }

  @Cacheable("inbound_transfers_paging")
  public Page<InboundResponse> getAllWithPaging(InboundFilterRequest request) {
    Sort sort = Sort.by(Sort.Direction.fromString(request.getSortDirection()), request.getSortBy());

    PageRequest pageable = PageRequest.of(request.getPage() - 1, request.getPageSize(), sort);

    return inboundTransferRepository
        .filterWithPaging(
            request.getStatus(),
            request.getFromDate(),
            request.getToDate(),
            request.getUserId(),
            request.getInventoryId(),
            request.getTransferId(),
            pageable)
        .map(inboundMapper::toInboundResponse);
  }
}
