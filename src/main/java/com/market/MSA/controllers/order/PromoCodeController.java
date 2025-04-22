package com.market.MSA.controllers.order;

import com.market.MSA.constants.ApiMessage;
import com.market.MSA.requests.order.PromoCodeRequest;
import com.market.MSA.responses.order.PromoCodeResponse;
import com.market.MSA.responses.others.ApiResponse;
import com.market.MSA.services.order.PromoCodeService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
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
@RequestMapping("/promo-code")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PromoCodeController {
  PromoCodeService promoCodeService;

  // Tạo PromoCode
  @PostMapping
  public ApiResponse<PromoCodeResponse> createPromoCode(
      @RequestBody @Valid PromoCodeRequest request) {
    return ApiResponse.<PromoCodeResponse>builder()
        .result(promoCodeService.createPromoCode(request))
        .message(ApiMessage.PROMO_CODE_CREATED.getMessage())
        .build();
  }

  // Cập nhật PromoCode
  @PutMapping("/{promoCodeId}")
  public ApiResponse<PromoCodeResponse> updatePromoCode(
      @PathVariable long promoCodeId, @RequestBody @Valid PromoCodeRequest request) {
    return ApiResponse.<PromoCodeResponse>builder()
        .result(promoCodeService.updatePromoCode(promoCodeId, request))
        .message(ApiMessage.PROMO_CODE_UPDATED.getMessage())
        .build();
  }

  // Xóa PromoCode
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
  @GetMapping("/code/{code}")
  public ApiResponse<PromoCodeResponse> getPromoCodeByCode(@PathVariable String code) {
    return ApiResponse.<PromoCodeResponse>builder()
        .result(promoCodeService.getPromoCodeByCode(code))
        .message(ApiMessage.PROMO_CODE_RETRIEVED.getMessage())
        .build();
  }

  // Lấy danh sách tất cả PromoCode
  @GetMapping
  public ApiResponse<List<PromoCodeResponse>> getAllPromoCodes(
      @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int pageSize) {
    return ApiResponse.<List<PromoCodeResponse>>builder()
        .result(promoCodeService.getAllPromoCodes(page, pageSize))
        .message(ApiMessage.ALL_PROMO_CODES_RETRIEVED.getMessage())
        .build();
  }
}
