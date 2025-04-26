package com.market.MSA.services.product;

import com.market.MSA.exceptions.AppException;
import com.market.MSA.exceptions.ErrorCode;
import com.market.MSA.mappers.product.TransferRequestItemMapper;
import com.market.MSA.models.product.TransferItem;
import com.market.MSA.repositories.product.ProductRepository;
import com.market.MSA.repositories.product.TransferRequestItemRepository;
import com.market.MSA.repositories.product.TransferRequestRepository;
import com.market.MSA.requests.product.TransferRequestItem;
import com.market.MSA.responses.product.TransferResponseItem;
import com.market.MSA.services.others.EntityFinderService;
import java.util.List;
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
public class TransferRequestItemService {

  final TransferRequestItemMapper transferRequestItemMapper;
  final EntityFinderService entityFinderService;
  final ProductRepository productRepository;
  final TransferRequestRepository transferRequestRepository;
  final TransferRequestItemRepository transferRequestItemRepository;

  @Transactional
  public TransferResponseItem createTransferRequestItem(TransferRequestItem transferRequestItem) {
    TransferItem transferItem =
        transferRequestItemMapper.toTransferRequestItem(transferRequestItem);
    transferItem.setProduct(
        entityFinderService.findByIdOrThrow(
            productRepository, transferRequestItem.getProductId(), ErrorCode.PRODUCT_NOT_FOUND));
    transferItem.setTransfer(
        entityFinderService.findByIdOrThrow(
            transferRequestRepository,
            transferRequestItem.getTransferRequestId(),
            ErrorCode.TRANSFER_REQUEST_NOT_FOUND));

    TransferItem savedTransferItem = transferRequestItemRepository.save(transferItem);
    return transferRequestItemMapper.toTransferResponseItem(savedTransferItem);
  }

  @Transactional
  public TransferResponseItem updateTransferRequestItem(
      Long transferItemId, TransferRequestItem transferRequestItem) {
    TransferItem transferItem =
        transferRequestItemRepository
            .findById(transferItemId)
            .orElseThrow(() -> new AppException(ErrorCode.TRANSFER_REQUEST_ITEM_NOT_FOUND));
    transferItem.setProduct(
        entityFinderService.findByIdOrThrow(
            productRepository, transferRequestItem.getProductId(), ErrorCode.PRODUCT_NOT_FOUND));
    transferItem.setTransfer(
        entityFinderService.findByIdOrThrow(
            transferRequestRepository,
            transferRequestItem.getTransferRequestId(),
            ErrorCode.TRANSFER_REQUEST_NOT_FOUND));

    transferRequestItemMapper.updateTransferRequestItem(transferRequestItem, transferItem);
    TransferItem updatedTransferItem = transferRequestItemRepository.save(transferItem);
    return transferRequestItemMapper.toTransferResponseItem(updatedTransferItem);
  }

  public boolean deleteTransferRequestItem(Long transferItemId) {
    if (!transferRequestItemRepository.existsById(transferItemId)) {
      throw new AppException(ErrorCode.TRANSFER_REQUEST_ITEM_NOT_FOUND);
    }
    transferRequestItemRepository.deleteById(transferItemId);
    return true;
  }

  public TransferResponseItem getTransferRequestItemById(Long transferItemId) {
    return transferRequestItemMapper.toTransferResponseItem(
        transferRequestItemRepository
            .findById(transferItemId)
            .orElseThrow(() -> new AppException(ErrorCode.TRANSFER_REQUEST_ITEM_NOT_FOUND)));
  }

  public List<TransferResponseItem> getAllTransferRequestItems(int page, int pageSize) {
    return transferRequestItemRepository.findAll().stream()
        .skip((long) (page - 1) * pageSize)
        .limit(pageSize)
        .map(transferRequestItemMapper::toTransferResponseItem)
        .collect(Collectors.toList());
  }

  public List<TransferResponseItem> getTransferRequestItemsByProductId(
      Long productId, int page, int pageSize) {
    Pageable pageable = PageRequest.of(page, pageSize);
    return transferRequestItemRepository
        .findByProduct_ProductIdWithPageable(productId, pageable)
        .stream()
        .map(transferRequestItemMapper::toTransferResponseItem)
        .collect(Collectors.toList());
  }

  public List<TransferResponseItem> getTransferRequestItemsByTransferRequestId(
      Long transferRequestId, int page, int pageSize) {
    Pageable pageable = PageRequest.of(page, pageSize);
    return transferRequestItemRepository
        .findByTransferRequest_TransferRequestIdWithPageable(transferRequestId, pageable)
        .stream()
        .map(transferRequestItemMapper::toTransferResponseItem)
        .collect(Collectors.toList());
  }
}
