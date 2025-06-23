package com.market.MSA.controllers.order;

import com.market.MSA.constants.ApiMessage;
import com.market.MSA.requests.filters.PromoCodeUsageFilterRequest;
import com.market.MSA.requests.order.PromoCodeUsageRequest;
import com.market.MSA.responses.order.PromoCodeResponse;
import com.market.MSA.responses.order.PromoCodeUsageResponse;
import com.market.MSA.responses.others.ApiResponse;
import com.market.MSA.services.order.PromoCodeUsageService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usage")
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PromoCodeUsageController {
  PromoCodeUsageService promoCodeUsageService;

  @PostMapping
  public ApiResponse<PromoCodeUsageResponse> createPromoCodeUsage(
      @RequestBody @Valid PromoCodeUsageRequest request) {
    return ApiResponse.<PromoCodeUsageResponse>builder()
        .result(promoCodeUsageService.createPromoCodeUsage(request))
        .message(ApiMessage.PROMO_CODE_USAGE_CREATED.getMessage())
        .build();
  }

  @PutMapping("/{usageId}")
  public ApiResponse<PromoCodeUsageResponse> updatePromoCodeUsage(
      @PathVariable long usageId, @RequestBody @Valid PromoCodeUsageRequest request) {
    return ApiResponse.<PromoCodeUsageResponse>builder()
        .result(promoCodeUsageService.updatePromoCodeUsage(usageId, request))
        .message(ApiMessage.PROMO_CODE_USAGE_UPDATED.getMessage())
        .build();
  }

  @DeleteMapping("/{usageId}")
  public ApiResponse<Boolean> deletePromoCodeUsage(@PathVariable long usageId) {
    Boolean result = promoCodeUsageService.deletePromoCodeUsage(usageId);
    return ApiResponse.<Boolean>builder()
        .result(result)
        .message(ApiMessage.PROMO_CODE_USAGE_DELETED.getMessage())
        .build();
  }

  @GetMapping("/{usageId}")
  public ApiResponse<PromoCodeUsageResponse> getPromoCodeUsageById(@PathVariable long usageId) {
    return ApiResponse.<PromoCodeUsageResponse>builder()
        .result(promoCodeUsageService.getPromoCodeUsageById(usageId))
        .message(ApiMessage.PROMO_CODE_USAGE_RETRIEVED.getMessage())
        .build();
  }

  @GetMapping
  public ApiResponse<List<PromoCodeUsageResponse>> getAll() {
    return ApiResponse.<List<PromoCodeUsageResponse>>builder()
            .result(promoCodeUsageService.getAll())
            .message(ApiMessage.ALL_PROMO_CODE_USAGES_RETRIEVED.getMessage())
            .build();
  }

  @PostMapping("/list")
  public ApiResponse<List<PromoCodeUsageResponse>> getAllPromoCodeUsages(
          @RequestBody @Valid PromoCodeUsageFilterRequest request) {
    return ApiResponse.<List<PromoCodeUsageResponse>>builder()
        .result(promoCodeUsageService.getAllPromoCodeUsages(request))
        .message(ApiMessage.ALL_PROMO_CODE_USAGES_RETRIEVED.getMessage())
        .build();
  }

  @PostMapping("/paging")
  public ApiResponse<Page<PromoCodeUsageResponse>> getAllPromoCodeUsagesWithPaging(
          @RequestBody @Valid PromoCodeUsageFilterRequest request) {
    return ApiResponse.<Page<PromoCodeUsageResponse>>builder()
        .result(promoCodeUsageService.getAllPromoCodeUsagesWithPaging(request))
        .message(ApiMessage.ALL_PROMO_CODE_USAGES_RETRIEVED.getMessage())
        .build();
  }
}
