package com.market.MSA.services;

import com.market.MSA.exceptions.AppException;
import com.market.MSA.exceptions.ErrorCode;
import com.market.MSA.mappers.StockTransferMapper;
import com.market.MSA.models.StockTransfer;
import com.market.MSA.repositories.BranchRepository;
import com.market.MSA.repositories.ProductRepository;
import com.market.MSA.repositories.StockTransferRepository;
import com.market.MSA.repositories.UserRepository;
import com.market.MSA.requests.StockTransferRequest;
import com.market.MSA.responses.StockTransferResponse;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class StockTransferService {

  private final StockTransferMapper stockTransferMapper;
  private final EntityFinderService entityFinderService;
  private final ProductRepository productRepository;
  private final BranchRepository branchRepository;
  private final UserRepository userRepository;
  private final StockTransferRepository stockTransferRepository;

  @Transactional
  public StockTransferResponse createTransferStock(StockTransferRequest request) {
    StockTransfer stockTransfer = stockTransferMapper.toStockTransfer(request);

    stockTransfer.setProduct(
        entityFinderService.findByIdOrThrow(
            productRepository, request.getProductId(), ErrorCode.PRODUCT_NOT_FOUND));
    stockTransfer.setFromBranch(
        entityFinderService.findByIdOrThrow(
            branchRepository, request.getFromBranchId(), ErrorCode.BRANCH_NOT_FOUND));
    stockTransfer.setToBranch(
        entityFinderService.findByIdOrThrow(
            branchRepository, request.getToBranchId(), ErrorCode.BRANCH_NOT_FOUND));
    stockTransfer.setUserRequest(
        entityFinderService.findByIdOrThrow(
            userRepository, request.getUserRequestId(), ErrorCode.USER_NOT_EXISTED));
    stockTransfer.setUserResponse(
        entityFinderService.findByIdOrThrow(
            userRepository, request.getUserResponseId(), ErrorCode.USER_NOT_EXISTED));

    stockTransferRepository.save(stockTransfer);
    return stockTransferMapper.toStockTransferResponse(stockTransfer);
  }

  @Transactional
  public StockTransferResponse updateStock(Long id, StockTransferRequest request) {
    StockTransfer stockTransfer =
        stockTransferRepository
            .findById(id)
            .orElseThrow(() -> new AppException(ErrorCode.STOCK_TRANSFER_NOT_FOUND));

    stockTransfer.setProduct(
        entityFinderService.findByIdOrThrow(
            productRepository, request.getProductId(), ErrorCode.PRODUCT_NOT_FOUND));
    stockTransfer.setFromBranch(
        entityFinderService.findByIdOrThrow(
            branchRepository, request.getFromBranchId(), ErrorCode.BRANCH_NOT_FOUND));
    stockTransfer.setToBranch(
        entityFinderService.findByIdOrThrow(
            branchRepository, request.getToBranchId(), ErrorCode.BRANCH_NOT_FOUND));
    stockTransfer.setUserRequest(
        entityFinderService.findByIdOrThrow(
            userRepository, request.getUserRequestId(), ErrorCode.USER_NOT_EXISTED));
    stockTransfer.setUserResponse(
        entityFinderService.findByIdOrThrow(
            userRepository, request.getUserResponseId(), ErrorCode.USER_NOT_EXISTED));

    stockTransferMapper.updateStockTransfer(request, stockTransfer);
    stockTransferRepository.save(stockTransfer);

    return stockTransferMapper.toStockTransferResponse(stockTransfer);
  }

  @Transactional
  public boolean deleteStock(Long id) {
    if (!stockTransferRepository.existsById(id)) {
      throw new AppException(ErrorCode.STOCK_TRANSFER_NOT_FOUND);
    }
    stockTransferRepository.deleteById(id);
    return true;
  }

  public StockTransferResponse getStockTransferById(Long id) {
    StockTransfer stockTransfer =
        stockTransferRepository
            .findById(id)
            .orElseThrow(() -> new AppException(ErrorCode.STOCK_TRANSFER_NOT_FOUND));
    return stockTransferMapper.toStockTransferResponse(stockTransfer);
  }

  public List<StockTransferResponse> getStockTransferByFromBranchId(Long id, String status) {
    List<StockTransfer> transfers =
        stockTransferRepository.findByFromBranch_FromBranchId(id, status);

    return transfers.stream()
        .map(stockTransferMapper::toStockTransferResponse)
        .collect(Collectors.toList());
  }

  public List<StockTransferResponse> getStockTransferByToBranchId(Long id, String status) {
    List<StockTransfer> transfers = stockTransferRepository.findByToBranch_ToBranchId(id, status);

    return transfers.stream()
        .map(stockTransferMapper::toStockTransferResponse)
        .collect(Collectors.toList());
  }

  public List<StockTransferResponse> getStockTransferByUserRequestId(Long id, String status) {
    List<StockTransfer> transfers =
        stockTransferRepository.findByUserRequest_UserRequestId(id, status);

    return transfers.stream()
        .map(stockTransferMapper::toStockTransferResponse)
        .collect(Collectors.toList());
  }

  public List<StockTransferResponse> getStockTransferByUserResponseId(Long id, String status) {
    List<StockTransfer> transfers =
        stockTransferRepository.findByUserResponse_UserResponseId(id, status);

    return transfers.stream()
        .map(stockTransferMapper::toStockTransferResponse)
        .collect(Collectors.toList());
  }

  public List<StockTransferResponse> getStockTransferByProductId(Long id, String status) {
    List<StockTransfer> transfers = stockTransferRepository.findByProduct_ProductId(id, status);

    return transfers.stream()
        .map(stockTransferMapper::toStockTransferResponse)
        .collect(Collectors.toList());
  }
}
