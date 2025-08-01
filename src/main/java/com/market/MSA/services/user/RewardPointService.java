package com.market.MSA.services.user;

import com.market.MSA.constants.RewardPointTransactionType;
import com.market.MSA.exceptions.AppException;
import com.market.MSA.exceptions.ErrorCode;
import com.market.MSA.mappers.user.RewardPointMapper;
import com.market.MSA.models.user.RewardPoint;
import com.market.MSA.repositories.user.RewardPointRepository;
import com.market.MSA.repositories.user.UserRepository;
import com.market.MSA.requests.filters.RewardPointFilterRequest;
import com.market.MSA.requests.user.RewardPointRequest;
import com.market.MSA.requests.user.RewardPointTransactionRequest;
import com.market.MSA.responses.user.RewardPointResponse;
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
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class RewardPointService {
  final RewardPointMapper rewardPointMapper;
  final EntityFinderService entityFinderService;
  final UserRepository userRepository;
  final RewardPointRepository rewardPointRepository;
  final RewardPointTransactionService rewardPointTransactionService;

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

  @Cacheable("all_reward_points")
  public List<RewardPointResponse> getAll() {
    return rewardPointRepository.findAll().stream()
        .map(rewardPointMapper::toRewardPointResponse)
        .collect(Collectors.toList());
  }

  @Cacheable("reward_points_list")
  @Transactional(readOnly = true)
  public List<RewardPointResponse> getAllRewardPoints(RewardPointFilterRequest request) {
    Sort sort = Sort.by(Sort.Direction.fromString(request.getSortDirection()), request.getSortBy());
    return rewardPointRepository.filter(request.getUserId(), sort).stream()
        .map(rewardPointMapper::toRewardPointResponse)
        .collect(Collectors.toList());
  }

  @Cacheable("reward_points_paging")
  @Transactional(readOnly = true)
  public Page<RewardPointResponse> getAllRewardPointsWithPaging(RewardPointFilterRequest request) {
    Sort sort = Sort.by(Sort.Direction.fromString(request.getSortDirection()), request.getSortBy());

    PageRequest pageable = PageRequest.of(request.getPage() - 1, request.getPageSize(), sort);
    return rewardPointRepository
        .filterWithPaging(request.getUserId(), pageable)
        .map(rewardPointMapper::toRewardPointResponse);
  }

  @Transactional
  public RewardPointResponse earnPoints(Long userId, Long orderId, double amount) {
    // 1. Tìm hoặc tạo mới bản ghi điểm thưởng cho người dùng.
    RewardPoint rewardPoint =
        rewardPointRepository.findByUser_UserId(userId, PageRequest.of(0, 1)).stream()
            .findFirst()
            .orElseGet(
                () -> { // Nếu người dùng chưa có điểm, tạo mới.
                  RewardPoint newRewardPoint = new RewardPoint();
                  newRewardPoint.setUser(
                      entityFinderService.findByIdOrThrow(
                          userRepository, userId, ErrorCode.USER_NOT_EXISTED));
                  newRewardPoint.setPoints(0);
                  newRewardPoint.setTotalEarned(0);
                  newRewardPoint.setTotalRedeemed(0);
                  return newRewardPoint;
                });

    // 2. Cập nhật điểm.
    rewardPoint.setPoints(rewardPoint.getPoints() + amount); // Tăng điểm hiện có.
    rewardPoint.setTotalEarned(
        rewardPoint.getTotalEarned() + amount); // Tăng tổng điểm đã tích lũy.
    rewardPoint.setUpdatedAt(LocalDateTime.now());
    RewardPoint savedRewardPoint = rewardPointRepository.save(rewardPoint);

    // 3. Tạo một giao dịch ghi lại lịch sử cộng điểm.
    rewardPointTransactionService.createRewardPointTransaction(
        RewardPointTransactionRequest.builder()
            .userId(userId)
            .orderId(orderId)
            .pointChange(amount) // Số điểm thay đổi là dương
            .type(RewardPointTransactionType.EARN) // Loại giao dịch: Tích điểm
            .description("Earned points from order #" + orderId)
            .build());

    return rewardPointMapper.toRewardPointResponse(savedRewardPoint);
  }

  @Transactional
  public RewardPointResponse redeemPoints(
      Long userId, Long orderId, double pointsToRedeem, String description) {
    // 1. Lấy thông tin điểm thưởng của người dùng.
    RewardPoint rewardPoint =
        rewardPointRepository.findByUser_UserId(userId, PageRequest.of(0, 1)).stream()
            .findFirst()
            .orElseThrow(() -> new AppException(ErrorCode.REWARD_POINT_NOT_FOUND));

    // 2. Kiểm tra xem người dùng có đủ điểm để sử dụng không.
    if (rewardPoint.getPoints() < pointsToRedeem) {
      throw new AppException(ErrorCode.INSUFFICIENT_POINTS);
    }

    // 3. Cập nhật điểm.
    rewardPoint.setPoints(rewardPoint.getPoints() - pointsToRedeem); // Trừ điểm hiện có.
    rewardPoint.setTotalRedeemed(
        rewardPoint.getTotalRedeemed() + pointsToRedeem); // Tăng tổng điểm đã sử dụng.
    RewardPoint savedRewardPoint = rewardPointRepository.save(rewardPoint);

    // 4. Tạo giao dịch ghi lại lịch sử sử dụng điểm.
    rewardPointTransactionService.createRewardPointTransaction(
        RewardPointTransactionRequest.builder()
            .userId(userId)
            .orderId(orderId)
            .pointChange(-pointsToRedeem) // Số điểm thay đổi là âm
            .type(RewardPointTransactionType.REDEEM) // Loại giao dịch: Sử dụng điểm
            .description(description)
            .build());

    return rewardPointMapper.toRewardPointResponse(savedRewardPoint);
  }

  @Transactional
  public RewardPointResponse adjustPoints(Long userId, double adjustAmount, String reason) {
    RewardPoint rewardPoint =
        rewardPointRepository.findByUser_UserId(userId, PageRequest.of(0, 1)).stream()
            .findFirst()
            .orElseThrow(() -> new AppException(ErrorCode.REWARD_POINT_NOT_FOUND));

    // Cập nhật điểm.
    rewardPoint.setPoints(rewardPoint.getPoints() + adjustAmount);
    if (adjustAmount > 0) { // Nếu là cộng điểm
      rewardPoint.setTotalEarned(rewardPoint.getTotalEarned() + adjustAmount);
    } else { // Nếu là trừ điểm
      rewardPoint.setTotalRedeemed(rewardPoint.getTotalRedeemed() + Math.abs(adjustAmount));
    }
    RewardPoint savedRewardPoint = rewardPointRepository.save(rewardPoint);

    // Tạo giao dịch ghi lại lịch sử điều chỉnh.
    rewardPointTransactionService.createRewardPointTransaction(
        RewardPointTransactionRequest.builder()
            .userId(userId)
            .orderId(null) // Không liên quan đến đơn hàng cụ thể
            .pointChange(adjustAmount)
            .type(RewardPointTransactionType.ADJUST) // Loại giao dịch: Điều chỉnh
            .description(reason)
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
