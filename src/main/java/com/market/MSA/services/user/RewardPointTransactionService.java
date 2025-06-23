package com.market.MSA.services.user;

import com.market.MSA.exceptions.AppException;
import com.market.MSA.exceptions.ErrorCode;
import com.market.MSA.mappers.user.RewardPointTransactionMapper;
import com.market.MSA.models.user.RewardPointTransaction;
import com.market.MSA.repositories.order.OrderRepository;
import com.market.MSA.repositories.user.RewardPointTransactionRepository;
import com.market.MSA.repositories.user.UserRepository;
import com.market.MSA.requests.filters.RewardPointTransactionFilterRequest;
import com.market.MSA.requests.user.RewardPointTransactionRequest;
import com.market.MSA.responses.user.RewardPointResponse;
import com.market.MSA.responses.user.RewardPointTransactionResponse;
import com.market.MSA.services.others.EntityFinderService;
import java.time.LocalDateTime;
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
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class RewardPointTransactionService {
  final RewardPointTransactionMapper rewardPointTransactionMapper;
  final EntityFinderService entityFinderService;
  final UserRepository userRepository;
  final OrderRepository orderRepository;
  final RewardPointTransactionRepository rewardPointTransactionRepository;

  @Transactional
  public RewardPointTransactionResponse createRewardPointTransaction(
      RewardPointTransactionRequest rewardPointTransactionRequest) {
    RewardPointTransaction rewardPointTransaction =
        rewardPointTransactionMapper.toRewardPointTransaction(rewardPointTransactionRequest);
    rewardPointTransaction.setUser(
        entityFinderService.findByIdOrThrow(
            userRepository, rewardPointTransactionRequest.getUserId(), ErrorCode.USER_NOT_EXISTED));
    rewardPointTransaction.setOrder(
        entityFinderService.findByIdOrThrow(
            orderRepository,
            rewardPointTransactionRequest.getOrderId(),
            ErrorCode.ORDER_NOT_FOUND));
    RewardPointTransaction savedRewardPointTransaction =
        rewardPointTransactionRepository.save(rewardPointTransaction);
    return rewardPointTransactionMapper.toRewardPointTransactionResponse(
        savedRewardPointTransaction);
  }

  @Transactional
  public RewardPointTransactionResponse updateRewardPointTransaction(
      Long rewardPointTransactionId, RewardPointTransactionRequest rewardPointTransactionRequest) {
    RewardPointTransaction rewardPointTransaction =
        rewardPointTransactionRepository
            .findById(rewardPointTransactionId)
            .orElseThrow(() -> new AppException(ErrorCode.REWARD_POINT_TRANSACTION_NOT_FOUND));
    rewardPointTransaction.setUser(
        entityFinderService.findByIdOrThrow(
            userRepository, rewardPointTransactionRequest.getUserId(), ErrorCode.USER_NOT_EXISTED));
    rewardPointTransaction.setOrder(
        entityFinderService.findByIdOrThrow(
            orderRepository,
            rewardPointTransactionRequest.getOrderId(),
            ErrorCode.ORDER_NOT_FOUND));
    rewardPointTransactionMapper.updateRewardPointTransaction(
        rewardPointTransactionRequest, rewardPointTransaction);
    RewardPointTransaction updatedRewardPointTransaction =
        rewardPointTransactionRepository.save(rewardPointTransaction);
    return rewardPointTransactionMapper.toRewardPointTransactionResponse(
        updatedRewardPointTransaction);
  }

  @Transactional
  public boolean deleteRewardPointTransaction(Long rewardPointTransactionId) {
    if (!rewardPointTransactionRepository.existsById(rewardPointTransactionId)) {
      throw new AppException(ErrorCode.REWARD_POINT_TRANSACTION_NOT_FOUND);
    }
    rewardPointTransactionRepository.deleteById(rewardPointTransactionId);
    return true;
  }

  @Cacheable("all_reward_point_transactions")
  public List<RewardPointTransactionResponse> getAll() {
    return rewardPointTransactionRepository.findAll().stream().map(rewardPointTransactionMapper::toRewardPointTransactionResponse).collect(Collectors.toList());
  }

  @Cacheable("reward_point_transactions_list")
  public List<RewardPointTransactionResponse> getAllRewardPointTransactions(
      RewardPointTransactionFilterRequest request) {
    Sort sort = Sort.by(Sort.Direction.fromString(request.getSortDirection()), request.getSortBy());

    // Handle date range
    LocalDateTime fromDate = request.getFromDate();
    LocalDateTime toDate = request.getToDate();

    // If only one date is provided, set a default range
    if (fromDate != null && toDate == null) {
      toDate = LocalDateTime.now();
    } else if (fromDate == null && toDate != null) {
      fromDate = toDate.minusMonths(1); // Default to last month if only toDate is provided
    }

    return rewardPointTransactionRepository
        .filter(
            request.getUserId(),
            request.getOrderId(), // orderId not in filter yet
            fromDate,
            toDate,
            sort)
        .stream()
        .map(rewardPointTransactionMapper::toRewardPointTransactionResponse)
        .collect(Collectors.toList());
  }

  @Cacheable("reward_point_transactions_paging")
  public Page<RewardPointTransactionResponse> getAllRewardPointTransactionsWithPaging(
      RewardPointTransactionFilterRequest request) {
    Sort sort = Sort.by(Sort.Direction.fromString(request.getSortDirection()), request.getSortBy());

    // Handle date range
    LocalDateTime fromDate = request.getFromDate();
    LocalDateTime toDate = request.getToDate();

    // If only one date is provided, set a default range
    if (fromDate != null && toDate == null) {
      toDate = LocalDateTime.now();
    } else if (fromDate == null && toDate != null) {
      fromDate = toDate.minusMonths(1); // Default to last month if only toDate is provided
    }

    Pageable pageable = PageRequest.of(request.getPage() - 1, request.getPageSize(), sort);

    return rewardPointTransactionRepository
        .filterWithPaging(
            request.getUserId(),
            request.getOrderId(), // orderId not in filter yet
            fromDate,
            toDate,
            pageable)
        .map(rewardPointTransactionMapper::toRewardPointTransactionResponse);
  }
}
