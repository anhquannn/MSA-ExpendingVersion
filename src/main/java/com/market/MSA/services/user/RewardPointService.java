package com.market.MSA.services.user;

import com.market.MSA.constants.RewardPointTransactionType;
import com.market.MSA.exceptions.AppException;
import com.market.MSA.exceptions.ErrorCode;
import com.market.MSA.mappers.user.RewardPointMapper;
import com.market.MSA.models.user.RewardPoint;
import com.market.MSA.repositories.user.RewardPointRepository;
import com.market.MSA.repositories.user.UserRepository;
import com.market.MSA.requests.user.RewardPointRequest;
import com.market.MSA.requests.user.RewardPointTransactionRequest;
import com.market.MSA.responses.user.RewardPointResponse;
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
public class RewardPointService {
  private final RewardPointMapper rewardPointMapper;
  private final EntityFinderService entityFinderService;
  private final UserRepository userRepository;
  private final RewardPointRepository rewardPointRepository;
  private final RewardPointTransactionService rewardPointTransactionService;

  @Transactional
  public RewardPointResponse createRewardPoint(RewardPointRequest rewardPointRequest) {
    RewardPoint rewardPoint = rewardPointMapper.toRewardPoint(rewardPointRequest);
    rewardPoint.setUser(
        entityFinderService.findByIdOrThrow(
            userRepository, rewardPointRequest.getUserId(), ErrorCode.USER_NOT_EXISTED));
    RewardPoint savedRewardPoint = rewardPointRepository.save(rewardPoint);
    return rewardPointMapper.toRewardPointResponse(savedRewardPoint);
  }

  @Transactional
  public RewardPointResponse updateRewardPoint(
      Long rewardPointId, RewardPointRequest rewardPointRequest) {
    RewardPoint rewardPoint =
        rewardPointRepository
            .findById(rewardPointId)
            .orElseThrow(() -> new AppException(ErrorCode.REWARD_POINT_NOT_FOUND));
    rewardPoint.setUser(
        entityFinderService.findByIdOrThrow(
            userRepository, rewardPointRequest.getUserId(), ErrorCode.USER_NOT_EXISTED));
    rewardPointMapper.updateRewardPoint(rewardPointRequest, rewardPoint);
    RewardPoint updatedRewardPoint = rewardPointRepository.save(rewardPoint);
    return rewardPointMapper.toRewardPointResponse(updatedRewardPoint);
  }

  @Transactional
  public boolean deleteRewardPoint(Long rewardPointId) {
    if (!rewardPointRepository.existsById(rewardPointId)) {
      throw new AppException(ErrorCode.REWARD_POINT_NOT_FOUND);
    }
    rewardPointRepository.deleteById(rewardPointId);
    return true;
  }

  public RewardPointResponse getRewardPointById(Long rewardPointId) {
    return rewardPointMapper.toRewardPointResponse(
        rewardPointRepository
            .findById(rewardPointId)
            .orElseThrow(() -> new AppException(ErrorCode.REWARD_POINT_NOT_FOUND)));
  }

  public List<RewardPointResponse> getAllRewardPoints(int page, int pageSize) {
    return rewardPointRepository.findAll().stream()
        .skip((long) (page - 1) * pageSize)
        .limit(pageSize)
        .map(rewardPointMapper::toRewardPointResponse)
        .collect(Collectors.toList());
  }

  public List<RewardPointResponse> getAllRewardPointsByUserId(Long userId, int page, int pageSize) {
    Pageable pageable = PageRequest.of(page, pageSize);
    return rewardPointRepository.findByUser_UserId(userId, pageable).stream()
        .map(rewardPointMapper::toRewardPointResponse)
        .collect(Collectors.toList());
  }

  @Transactional
  public RewardPointResponse earnPoints(Long userId, Long orderId, double amount) {
    // Get or create reward point record for user
    RewardPoint rewardPoint =
        rewardPointRepository.findByUser_UserId(userId, PageRequest.of(0, 1)).stream()
            .findFirst()
            .orElseGet(
                () -> {
                  RewardPoint newRewardPoint = new RewardPoint();
                  newRewardPoint.setUser(
                      entityFinderService.findByIdOrThrow(
                          userRepository, userId, ErrorCode.USER_NOT_EXISTED));
                  newRewardPoint.setPoints(0);
                  newRewardPoint.setTotalEarned(0);
                  newRewardPoint.setTotalRedeemed(0);
                  newRewardPoint.setUpdatedAt(new Date());
                  return newRewardPoint;
                });

    // Calculate points (1 point per dollar)

    // Update reward point record
    rewardPoint.setPoints(rewardPoint.getPoints() + amount);
    rewardPoint.setTotalEarned(rewardPoint.getTotalEarned() + amount);
    rewardPoint.setUpdatedAt(new Date());
    RewardPoint savedRewardPoint = rewardPointRepository.save(rewardPoint);

    // Create transaction record
    rewardPointTransactionService.createRewardPointTransaction(
        RewardPointTransactionRequest.builder()
            .userId(userId)
            .orderId(orderId)
            .pointChange(amount)
            .type(RewardPointTransactionType.EARN.getValue())
            .description("Earned points from order #" + orderId)
            .createdAt(new Date())
            .build());

    return rewardPointMapper.toRewardPointResponse(savedRewardPoint);
  }

  @Transactional
  public RewardPointResponse redeemPoints(Long userId, double pointsToRedeem, String description) {
    // Get reward point record
    RewardPoint rewardPoint =
        rewardPointRepository.findByUser_UserId(userId, PageRequest.of(0, 1)).stream()
            .findFirst()
            .orElseThrow(() -> new AppException(ErrorCode.REWARD_POINT_NOT_FOUND));

    // Check if user has enough points
    if (rewardPoint.getPoints() < pointsToRedeem) {
      throw new AppException(ErrorCode.INSUFFICIENT_POINTS);
    }

    // Update reward point record
    rewardPoint.setPoints(rewardPoint.getPoints() - pointsToRedeem);
    rewardPoint.setTotalRedeemed(rewardPoint.getTotalRedeemed() + pointsToRedeem);
    rewardPoint.setUpdatedAt(new Date());
    RewardPoint savedRewardPoint = rewardPointRepository.save(rewardPoint);

    // Create transaction record
    rewardPointTransactionService.createRewardPointTransaction(
        RewardPointTransactionRequest.builder()
            .userId(userId)
            .orderId(null) // No order associated with redemption
            .pointChange(-pointsToRedeem)
            .type(RewardPointTransactionType.REDEEM.getValue())
            .description(description)
            .createdAt(new Date())
            .build());

    return rewardPointMapper.toRewardPointResponse(savedRewardPoint);
  }

  @Transactional
  public RewardPointResponse adjustPoints(Long userId, double adjustAmount, String reason) {
    // Get reward point record
    RewardPoint rewardPoint =
        rewardPointRepository.findByUser_UserId(userId, PageRequest.of(0, 1)).stream()
            .findFirst()
            .orElseThrow(() -> new AppException(ErrorCode.REWARD_POINT_NOT_FOUND));

    // Update reward point record
    rewardPoint.setPoints(rewardPoint.getPoints() + adjustAmount);
    if (adjustAmount > 0) {
      rewardPoint.setTotalEarned(rewardPoint.getTotalEarned() + adjustAmount);
    } else {
      rewardPoint.setTotalRedeemed(rewardPoint.getTotalRedeemed() + Math.abs(adjustAmount));
    }
    rewardPoint.setUpdatedAt(new Date());
    RewardPoint savedRewardPoint = rewardPointRepository.save(rewardPoint);

    // Create transaction record
    rewardPointTransactionService.createRewardPointTransaction(
        RewardPointTransactionRequest.builder()
            .userId(userId)
            .orderId(null) // No order associated with adjustment
            .pointChange(adjustAmount)
            .type(RewardPointTransactionType.ADJUST.getValue())
            .description(reason)
            .createdAt(new Date())
            .build());

    return rewardPointMapper.toRewardPointResponse(savedRewardPoint);
  }

  public double getAvailablePoints(Long userId) {
    return rewardPointRepository.findByUser_UserId(userId, PageRequest.of(0, 1)).stream()
        .findFirst()
        .map(RewardPoint::getPoints)
        .orElse(0.0);
  }
}
