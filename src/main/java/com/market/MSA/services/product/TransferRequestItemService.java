package com.market.MSA.services.product;

import com.market.MSA.exceptions.AppException;
import com.market.MSA.exceptions.ErrorCode;
import com.market.MSA.mappers.product.TransferRequestItemMapper;
import com.market.MSA.models.product.TransferItem;
import com.market.MSA.repositories.product.ProductRepository;
import com.market.MSA.repositories.product.TransferRequestItemRepository;
import com.market.MSA.repositories.product.TransferRequestRepository;
import com.market.MSA.requests.filters.TransferRequestItemFilterRequest;
import com.market.MSA.requests.product.TransferRequestItem;
import com.market.MSA.responses.product.SupplierResponse;
import com.market.MSA.responses.product.TransferResponseItem;
import com.market.MSA.services.others.EntityFinderService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
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

  @Cacheable("all_transfer_request_items")
  public List<TransferResponseItem> getAll() {
    return transferRequestItemRepository.findAll().stream().map(transferRequestItemMapper::toTransferResponseItem).collect(Collectors.toList());
  }

  @Cacheable("transfer_request_items")
  public List<TransferResponseItem> getAllTransferRequestItems(
      TransferRequestItemFilterRequest request) {
    return transferRequestItemRepository
        .filter(request.getTransferRequestId(), request.getProductId())
        .stream()
        .map(transferRequestItemMapper::toTransferResponseItem)
        .collect(Collectors.toList());
  }

  @Cacheable("transfer_request_items")
  public Page<TransferResponseItem> getAllTransferRequestItemsWithPaging(
      TransferRequestItemFilterRequest request) {
    Sort sort = Sort.by(Sort.Direction.fromString(request.getSortDirection()), request.getSortBy());

    Pageable pageable = PageRequest.of(request.getPage() - 1, request.getPageSize(), sort);

    return transferRequestItemRepository
        .filterWithPaging(request.getTransferRequestId(), request.getProductId(), pageable)
        .map(transferRequestItemMapper::toTransferResponseItem);
  }
}
