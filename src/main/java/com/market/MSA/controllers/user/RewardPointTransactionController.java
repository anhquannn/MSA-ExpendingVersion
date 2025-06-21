package com.market.MSA.controllers.user;

import com.market.MSA.constants.ApiMessage;
import com.market.MSA.requests.filters.RewardPointTransactionFilterRequest;
import com.market.MSA.requests.user.RewardPointTransactionRequest;
import com.market.MSA.responses.others.ApiResponse;
import com.market.MSA.responses.user.RewardPointResponse;
import com.market.MSA.responses.user.RewardPointTransactionResponse;
import com.market.MSA.services.user.RewardPointTransactionService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rpt")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class RewardPointTransactionController {
  RewardPointTransactionService rewardPointTransactionService;

  @PostMapping
  public ApiResponse<RewardPointTransactionResponse> createRewardPointTransaction(
      @Valid @RequestBody RewardPointTransactionRequest request) {
    return ApiResponse.<RewardPointTransactionResponse>builder()
        .result(rewardPointTransactionService.createRewardPointTransaction(request))
        .message(ApiMessage.REWARD_POINT_TRANSACTION_CREATED.getMessage())
        .build();
  }

  @PutMapping("/{transactionId}")
  public ApiResponse<RewardPointTransactionResponse> updateRewardPointTransaction(
      @PathVariable Long transactionId, @Valid @RequestBody RewardPointTransactionRequest request) {
    return ApiResponse.<RewardPointTransactionResponse>builder()
        .result(rewardPointTransactionService.updateRewardPointTransaction(transactionId, request))
        .message(ApiMessage.REWARD_POINT_TRANSACTION_UPDATED.getMessage())
        .build();
  }

  @DeleteMapping("/{transactionId}")
  public ApiResponse<Boolean> deleteRewardPointTransaction(@PathVariable Long transactionId) {
    rewardPointTransactionService.deleteRewardPointTransaction(transactionId);
    return ApiResponse.<Boolean>builder()
        .result(true)
        .message(ApiMessage.REWARD_POINT_TRANSACTION_DELETED.getMessage())
        .build();
  }

  @GetMapping
  public ApiResponse<List<RewardPointTransactionResponse>> getAll() {
    return ApiResponse.<List<RewardPointTransactionResponse>>builder()
            .result(rewardPointTransactionService.getAll())
            .message(ApiMessage.ALL_REWARD_POINT_TRANSACTIONS_RETRIEVED.getMessage())
            .build();
  }

  @PostMapping("/list")
  public ApiResponse<List<RewardPointTransactionResponse>> filterTransactions(
      @Valid @RequestBody RewardPointTransactionFilterRequest request) {
    return ApiResponse.<List<RewardPointTransactionResponse>>builder()
        .result(rewardPointTransactionService.getAllRewardPointTransactions(request))
        .message(ApiMessage.ALL_REWARD_POINT_TRANSACTIONS_RETRIEVED.getMessage())
        .build();
  }

  @PostMapping("/paging")
  public ApiResponse<Page<RewardPointTransactionResponse>> filterTransactionsWithPaging(
      @Valid @RequestBody RewardPointTransactionFilterRequest request) {
    return ApiResponse.<Page<RewardPointTransactionResponse>>builder()
        .result(rewardPointTransactionService.getAllRewardPointTransactionsWithPaging(request))
        .message(ApiMessage.ALL_REWARD_POINT_TRANSACTIONS_RETRIEVED.getMessage())
        .build();
  }
}
