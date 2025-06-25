package com.market.MSA.controllers.user;

import com.market.MSA.constants.ApiMessage;
import com.market.MSA.requests.filters.RewardPointFilterRequest;
import com.market.MSA.requests.user.RewardPointRequest;
import com.market.MSA.responses.others.ApiResponse;
import com.market.MSA.responses.user.RewardPointResponse;
import com.market.MSA.services.user.RewardPointService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rp")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class RewardPointController {
  RewardPointService rewardPointService;

  @PostMapping
  public ApiResponse<RewardPointResponse> createRewardPoint(
      @Valid @RequestBody RewardPointRequest request) {
    return ApiResponse.<RewardPointResponse>builder()
        .result(rewardPointService.createRewardPoint(request))
        .message(ApiMessage.REWARD_POINT_CREATED.getMessage())
        .build();
  }

  @PutMapping("/{rewardPointId}")
  public ApiResponse<RewardPointResponse> updateRewardPoint(
      @PathVariable Long rewardPointId, @Valid @RequestBody RewardPointRequest request) {
    return ApiResponse.<RewardPointResponse>builder()
        .result(rewardPointService.updateRewardPoint(rewardPointId, request))
        .message(ApiMessage.REWARD_POINT_UPDATED.getMessage())
        .build();
  }

  @DeleteMapping("/{rewardPointId}")
  public ApiResponse<Boolean> deleteRewardPoint(@PathVariable Long rewardPointId) {
    rewardPointService.deleteRewardPoint(rewardPointId);
    return ApiResponse.<Boolean>builder()
        .result(true)
        .message(ApiMessage.REWARD_POINT_DELETED.getMessage())
        .build();
  }

  @GetMapping("/{rewardPointId}")
  public ApiResponse<RewardPointResponse> getRewardPointById(@PathVariable Long rewardPointId) {
    return ApiResponse.<RewardPointResponse>builder()
        .result(rewardPointService.getRewardPointById(rewardPointId))
        .message(ApiMessage.REWARD_POINT_RETRIEVED.getMessage())
        .build();
  }

  @GetMapping
  public ApiResponse<List<RewardPointResponse>> getAll() {
    return ApiResponse.<List<RewardPointResponse>>builder()
        .result(rewardPointService.getAll())
        .message(ApiMessage.ALL_REWARD_POINTS_RETRIEVED.getMessage())
        .build();
  }

  @PostMapping("/list")
  public ApiResponse<List<RewardPointResponse>> getAllRewardPoint(
      @RequestBody @Valid RewardPointFilterRequest request) {
    return ApiResponse.<List<RewardPointResponse>>builder()
        .result(rewardPointService.getAllRewardPoints(request))
        .message(ApiMessage.ALL_REWARD_POINTS_RETRIEVED.getMessage())
        .build();
  }

  @PostMapping("/paging")
  public ApiResponse<Page<RewardPointResponse>> getAllRewardPointWithPaging(
      @RequestBody @Valid RewardPointFilterRequest request) {
    return ApiResponse.<Page<RewardPointResponse>>builder()
        .result(rewardPointService.getAllRewardPointsWithPaging(request))
        .message(ApiMessage.ALL_REWARD_POINTS_RETRIEVED.getMessage())
        .build();
  }

  @GetMapping("/user/{userId}/balance")
  public ApiResponse<Double> getAvailablePoints(@PathVariable Long userId) {
    return ApiResponse.<Double>builder()
        .result(rewardPointService.getAvailablePoints(userId))
        .message(ApiMessage.REWARD_POINT_RETRIEVED.getMessage())
        .build();
  }

  @PostMapping("/earn")
  public ApiResponse<RewardPointResponse> earnPoints(
      @RequestParam Long userId, @RequestParam Long orderId, @RequestParam double amount) {
    return ApiResponse.<RewardPointResponse>builder()
        .result(rewardPointService.earnPoints(userId, orderId, amount))
        .message(ApiMessage.POINTS_EARNED.getMessage())
        .build();
  }

  @PostMapping("/redeem")
  public ApiResponse<RewardPointResponse> redeemPoints(
      @RequestParam Long userId,
      @RequestParam double pointsToRedeem,
      @RequestParam(required = false) String description) {
    return ApiResponse.<RewardPointResponse>builder()
        .result(rewardPointService.redeemPoints(userId, pointsToRedeem, description))
        .message(ApiMessage.POINTS_REDEEMED.getMessage())
        .build();
  }

  @PostMapping("/adjust")
  public ApiResponse<RewardPointResponse> adjustPoints(
      @RequestParam Long userId, @RequestParam double adjustAmount, @RequestParam String reason) {
    return ApiResponse.<RewardPointResponse>builder()
        .result(rewardPointService.adjustPoints(userId, adjustAmount, reason))
        .message(ApiMessage.POINTS_ADJUSTED.getMessage())
        .build();
  }
}
