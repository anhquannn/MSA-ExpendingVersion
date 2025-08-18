package com.market.MSA.controllers.order;

import com.market.MSA.constants.ApiMessage;
import com.market.MSA.constants.PromocodeStatus;
import com.market.MSA.requests.filters.PromoCodeFilterRequest;
import com.market.MSA.requests.order.PromoCodeRequest;
import com.market.MSA.responses.order.PromoCodeResponse;
import com.market.MSA.responses.others.ApiResponse;
import com.market.MSA.services.order.PromoCodeService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/promo-code")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PromoCodeController {
  PromoCodeService promoCodeService;

  // Tạo PromoCode
  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping
  public ApiResponse<PromoCodeResponse> createPromoCode(
      @RequestBody @Valid PromoCodeRequest request) {
    return ApiResponse.<PromoCodeResponse>builder()
        .result(promoCodeService.createPromoCode(request))
        .message(ApiMessage.PROMO_CODE_CREATED.getMessage())
        .build();
  }

  // Cập nhật PromoCode
  @PreAuthorize("hasRole('ADMIN')")
  @PutMapping("/{promoCodeId}")
  public ApiResponse<PromoCodeResponse> updatePromoCode(
      @PathVariable long promoCodeId, @RequestBody @Valid PromoCodeRequest request) {
    return ApiResponse.<PromoCodeResponse>builder()
        .result(promoCodeService.updatePromoCode(promoCodeId, request))
        .message(ApiMessage.PROMO_CODE_UPDATED.getMessage())
        .build();
  }

  // Xóa PromoCode
  @PreAuthorize("hasRole('ADMIN')")
  @DeleteMapping("/{promoCodeId}")
  public ApiResponse<Boolean> deletePromoCode(@PathVariable long promoCodeId) {
    Boolean result = promoCodeService.deletePromoCode(promoCodeId);
    return ApiResponse.<Boolean>builder()
        .result(result)
        .message(ApiMessage.PROMO_CODE_DELETED.getMessage())
        .build();
  }

  // Lấy PromoCode theo ID
  @GetMapping("/{promoCodeId}")
  public ApiResponse<PromoCodeResponse> getPromoCodeById(@PathVariable long promoCodeId) {
    return ApiResponse.<PromoCodeResponse>builder()
        .result(promoCodeService.getPromoCodeById(promoCodeId))
        .message(ApiMessage.PROMO_CODE_RETRIEVED.getMessage())
        .build();
  }

  // Lấy PromoCode theo mã code
  @GetMapping("/user/{userId}/code/{code}")
  public ApiResponse<PromoCodeResponse> getPromoCodeByCode(
      @PathVariable String code, @PathVariable Long userId) {
    return ApiResponse.<PromoCodeResponse>builder()
        .result(promoCodeService.getPromoCodeByCode(code, userId))
        .message(ApiMessage.PROMO_CODE_RETRIEVED.getMessage())
        .build();
  }

  @GetMapping
  public ApiResponse<List<PromoCodeResponse>> getAll() {
    return ApiResponse.<List<PromoCodeResponse>>builder()
        .result(promoCodeService.getAll())
        .message(ApiMessage.ALL_PROMO_CODES_RETRIEVED.getMessage())
        .build();
  }

  // Lấy danh sách tất cả PromoCode (không phân trang)
  @PostMapping("/list")
  public ApiResponse<List<PromoCodeResponse>> getAllPromoCodes(
      @RequestBody @Valid PromoCodeFilterRequest request) {
    return ApiResponse.<List<PromoCodeResponse>>builder()
        .result(promoCodeService.getAllPromoCodes(request))
        .message(ApiMessage.ALL_PROMO_CODES_RETRIEVED.getMessage())
        .build();
  }

  // Lấy danh sách tất cả PromoCode (có phân trang)
  @PostMapping("/paging")
  public ApiResponse<Page<PromoCodeResponse>> getAllPromoCodesWithPaging(
      @RequestBody @Valid PromoCodeFilterRequest request) {
    return ApiResponse.<Page<PromoCodeResponse>>builder()
        .result(promoCodeService.getAllPromoCodesWithPaging(request))
        .message(ApiMessage.ALL_PROMO_CODES_RETRIEVED.getMessage())
        .build();
  }

  @GetMapping("/cart/active")
  public ApiResponse<List<PromoCodeResponse>> getActivePromoCodesForCart(
      @RequestParam Long cartId, @RequestParam(required = false) Long userId) {
    return ApiResponse.<List<PromoCodeResponse>>builder()
        .result(promoCodeService.getActivePromoCodesForCart(cartId, userId))
        .message(ApiMessage.ALL_PROMO_CODES_RETRIEVED.getMessage())
        .build();
  }

  @GetMapping("/cart/applicable")
  public ApiResponse<List<PromoCodeResponse>> getApplicablePromoCodesForCart(
      @RequestParam Long cartId,
      @RequestParam PromocodeStatus status,
      @RequestParam(required = false) Long userId) {
    return ApiResponse.<List<PromoCodeResponse>>builder()
        .result(promoCodeService.getApplicablePromoCodesForCart(cartId, status, userId))
        .message(ApiMessage.ALL_PROMO_CODES_RETRIEVED.getMessage())
        .build();
  }

  @PostMapping("/cart/list")
  public ApiResponse<List<PromoCodeResponse>> filterPromoCodesWithCart(
      @RequestBody @Valid PromoCodeFilterRequest request) {
    return ApiResponse.<List<PromoCodeResponse>>builder()
        .result(
            promoCodeService.filterPromoCodesWithCart(
                request.getKeyword(),
                request.getStatus(),
                request.getCampaignId(),
                request.getFromDate(),
                request.getToDate(),
                request.getCartId(),
                request.getUserId(),
                Sort.by(
                    Sort.Direction.fromString(request.getSortDirection()), request.getSortBy())))
        .message(ApiMessage.ALL_PROMO_CODES_RETRIEVED.getMessage())
        .build();
  }

  @PostMapping("/cart/paging")
  public ApiResponse<Page<PromoCodeResponse>> filterPromoCodesWithPagingAndCart(
      @RequestBody @Valid PromoCodeFilterRequest request) {
    Pageable pageable =
        PageRequest.of(
            request.getPage() - 1,
            request.getPageSize(),
            Sort.by(Sort.Direction.fromString(request.getSortDirection()), request.getSortBy()));

    return ApiResponse.<Page<PromoCodeResponse>>builder()
        .result(
            promoCodeService.filterPromoCodesWithPagingAndCart(
                request.getKeyword(),
                request.getStatus(),
                request.getCampaignId(),
                request.getFromDate(),
                request.getToDate(),
                request.getCartId(),
                request.getUserId(),
                pageable))
        .message(ApiMessage.ALL_PROMO_CODES_RETRIEVED.getMessage())
        .build();
  }
}
