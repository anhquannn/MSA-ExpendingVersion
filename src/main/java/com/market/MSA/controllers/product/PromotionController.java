package com.market.MSA.controllers.product;

import static com.market.MSA.constants.ApiMessage.*;

import com.market.MSA.requests.filters.PromotionFilterRequest;
import com.market.MSA.requests.product.PromotionRequest;
import com.market.MSA.responses.others.ApiResponse;
import com.market.MSA.responses.product.PromotionResponse;
import com.market.MSA.services.product.PromotionService;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/promotion")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PromotionController {

  PromotionService promotionService;

  @PostMapping
  public ApiResponse<PromotionResponse> create(@RequestBody PromotionRequest request) {
    return ApiResponse.<PromotionResponse>builder()
        .result(promotionService.createPromotion(request))
        .message(PROMOTION_CREATED.getMessage())
        .build();
  }

  @PutMapping("/{id}")
  public ApiResponse<PromotionResponse> update(
      @PathVariable Long id, @RequestBody PromotionRequest request) {
    return ApiResponse.<PromotionResponse>builder()
        .result(promotionService.updatePromotion(id, request))
        .message(PROMOTION_UPDATED.getMessage())
        .build();
  }

  @DeleteMapping("/{id}")
  public ApiResponse<Boolean> delete(@PathVariable Long id) {
    return ApiResponse.<Boolean>builder()
        .result(promotionService.deletePromotion(id))
        .message(PROMOTION_DELETED.getMessage())
        .build();
  }

  @GetMapping("/{id}")
  public ApiResponse<PromotionResponse> getById(@PathVariable Long id) {
    return ApiResponse.<PromotionResponse>builder()
        .result(promotionService.getPromotionById(id))
        .message(PROMOTION_RETRIEVED.getMessage())
        .build();
  }

  @GetMapping("/all")
  public ApiResponse<List<PromotionResponse>> getAll() {
    return ApiResponse.<List<PromotionResponse>>builder()
        .result(promotionService.getAll())
        .message(ALL_PROMOTIONS_RETRIEVED.getMessage())
        .build();
  }

  @PostMapping("/list")
  public ApiResponse<List<PromotionResponse>> getList(@RequestBody PromotionFilterRequest request) {
    return ApiResponse.<List<PromotionResponse>>builder()
        .result(promotionService.getAllPromotions(request))
        .message(PROMOTION_FILTERED_LIST_RETRIEVED.getMessage())
        .build();
  }

  @PostMapping("/paging")
  public ApiResponse<Page<PromotionResponse>> getPaging(
      @RequestBody PromotionFilterRequest request) {
    return ApiResponse.<Page<PromotionResponse>>builder()
        .result(promotionService.getAllPromotionsWithPaging(request))
        .message(PROMOTION_PAGING_RETRIEVED.getMessage())
        .build();
  }

  @PostMapping("/apply")
  public ApiResponse<Boolean> applyPromotions(@RequestParam Long cartId) {
    promotionService.applyBundlePromotions(cartId);
    return ApiResponse.<Boolean>builder()
        .result(true)
        .message(PROMOTION_APPLIED.getMessage())
        .build();
  }
}
