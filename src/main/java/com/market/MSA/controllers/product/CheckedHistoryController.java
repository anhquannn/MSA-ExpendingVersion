package com.market.MSA.controllers.product;

import com.market.MSA.constants.ApiMessage;
import com.market.MSA.requests.filters.CheckedHistoryFilterRequest;
import com.market.MSA.requests.product.CheckedHistoryRequest;
import com.market.MSA.responses.others.ApiResponse;
import com.market.MSA.responses.product.CheckedHistoryResponse;
import com.market.MSA.services.product.CheckedHistoryService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/checked-history")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CheckedHistoryController {
  CheckedHistoryService checkedHistoryService;

  @PostMapping
  public ApiResponse<CheckedHistoryResponse> createCheckedHistory(
      @RequestBody CheckedHistoryRequest request) {
    return ApiResponse.<CheckedHistoryResponse>builder()
        .result(checkedHistoryService.createCheckedHistory(request))
        .message(ApiMessage.CHECKED_HISTORY_CREATED.getMessage())
        .build();
  }

  @PutMapping("/{id}")
  public ApiResponse<CheckedHistoryResponse> updateCheckedHistory(
      @PathVariable Long id, @RequestBody CheckedHistoryRequest request) {
    return ApiResponse.<CheckedHistoryResponse>builder()
        .result(checkedHistoryService.updateCheckedHistory(id, request))
        .message(ApiMessage.CHECKED_HISTORY_UPDATED.getMessage())
        .build();
  }

  @DeleteMapping("/{id}")
  public ApiResponse<Boolean> deleteCheckedHistory(@PathVariable Long id) {
    Boolean result = checkedHistoryService.deleteCheckedHistory(id);
    return ApiResponse.<Boolean>builder()
        .result(result)
        .message(ApiMessage.CHECKED_HISTORY_DELETED.getMessage())
        .build();
  }

  @GetMapping("/{id}")
  public ApiResponse<CheckedHistoryResponse> getCheckedHistoryById(@PathVariable Long id) {
    return ApiResponse.<CheckedHistoryResponse>builder()
        .result(checkedHistoryService.getCheckedHistoryById(id))
        .message(ApiMessage.CHECKED_HISTORY_RETRIEVED.getMessage())
        .build();
  }

  @GetMapping
  public ApiResponse<List<CheckedHistoryResponse>> getAll() {
    return ApiResponse.<List<CheckedHistoryResponse>>builder()
        .result(checkedHistoryService.getAll())
        .message(ApiMessage.ALL_CHECKED_HISTORIES_RETRIEVED.getMessage())
        .build();
  }

  @PostMapping("/list")
  public ApiResponse<List<CheckedHistoryResponse>> getAllCheckedHistories(
      @RequestBody @Valid CheckedHistoryFilterRequest request) {
    return ApiResponse.<List<CheckedHistoryResponse>>builder()
        .result(checkedHistoryService.getAllCheckedHistories(request))
        .message(ApiMessage.ALL_CHECKED_HISTORIES_RETRIEVED.getMessage())
        .build();
  }

  @PostMapping("/paging")
  public ApiResponse<Page<CheckedHistoryResponse>> getAllCheckedHistoriesWithPaging(
      @RequestBody @Valid CheckedHistoryFilterRequest request) {
    return ApiResponse.<Page<CheckedHistoryResponse>>builder()
        .result(checkedHistoryService.getAllCheckedHistoriesWithPaging(request))
        .message(ApiMessage.ALL_CHECKED_HISTORIES_RETRIEVED.getMessage())
        .build();
  }
}
