package com.market.MSA.services.user;

import com.market.MSA.exceptions.AppException;
import com.market.MSA.exceptions.ErrorCode;
import com.market.MSA.mappers.user.RewardPointTransactionMapper;
import com.market.MSA.models.user.RewardPointTransaction;
import com.market.MSA.repositories.order.OrderRepository;
import com.market.MSA.repositories.user.RewardPointTransactionRepository;
import com.market.MSA.repositories.user.UserRepository;
import com.market.MSA.requests.user.RewardPointTransactionRequest;
import com.market.MSA.responses.user.RewardPointTransactionResponse;
import com.market.MSA.services.others.EntityFinderService;
import java.util.Date;
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
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class RewardPointTransactionService {
  private final RewardPointTransactionMapper rewardPointTransactionMapper;
  private final EntityFinderService entityFinderService;
  private final UserRepository userRepository;
  private final OrderRepository orderRepository;
  private final RewardPointTransactionRepository rewardPointTransactionRepository;

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

  public List<RewardPointTransactionResponse> getAllRewardPointTransactions(
      int page, int pageSize) {
    return rewardPointTransactionRepository.findAll().stream()
        .skip((long) (page - 1) * pageSize)
        .limit(pageSize)
        .map(rewardPointTransactionMapper::toRewardPointTransactionResponse)
        .collect(Collectors.toList());
  }

  public List<RewardPointTransactionResponse> getAllRewardPointTransactionsByUserId(
      Long userId, int page, int pageSize) {
    Pageable pageable = PageRequest.of(page, pageSize);
    return rewardPointTransactionRepository.findByUser_UserId(userId, pageable).stream()
        .map(rewardPointTransactionMapper::toRewardPointTransactionResponse)
        .collect(Collectors.toList());
  }

  public List<RewardPointTransactionResponse> getAllRewardPointTransactionsByOrderId(
      Long orderId, int page, int pageSize) {
    Pageable pageable = PageRequest.of(page, pageSize);
    return rewardPointTransactionRepository.findByOrder_OrderId(orderId, pageable).stream()
        .map(rewardPointTransactionMapper::toRewardPointTransactionResponse)
        .collect(Collectors.toList());
  }

  public List<RewardPointTransactionResponse> getRewardPointHistory(
      Long userId, Date fromDate, Date toDate, int page, int pageSize) {
    Pageable pageable = PageRequest.of(page, pageSize);
    return rewardPointTransactionRepository
        .findByUser_UserIdAndCreatedAtBetween(userId, fromDate, toDate, pageable)
        .stream()
        .map(rewardPointTransactionMapper::toRewardPointTransactionResponse)
        .collect(Collectors.toList());
  }
}
