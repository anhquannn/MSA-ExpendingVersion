package com.market.MSA.controllers;

import com.market.MSA.requests.PromoCodeRequest;
import com.market.MSA.responses.ApiResponse;
import com.market.MSA.responses.PromoCodeResponse;
import com.market.MSA.services.PromoCodeService;
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
@RequestMapping("/promocode")
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
        .build();
  }

  // Cập nhật PromoCode
  @PutMapping("/{promoCodeId}")
  public ApiResponse<PromoCodeResponse> updatePromoCode(
      @PathVariable long promoCodeId, @RequestBody @Valid PromoCodeRequest request) {
    return ApiResponse.<PromoCodeResponse>builder()
        .result(promoCodeService.updatePromoCode(promoCodeId, request))
        .build();
  }

  // Xóa PromoCode
  @DeleteMapping("/{promoCodeId}")
  public ApiResponse<String> deletePromoCode(@PathVariable long promoCodeId) {
    promoCodeService.deletePromoCode(promoCodeId);
    return ApiResponse.<String>builder().result("PromoCode has been deleted").build();
  }

  // Lấy PromoCode theo ID
  @GetMapping("/{promoCodeId}")
  public ApiResponse<PromoCodeResponse> getPromoCodeById(@PathVariable long promoCodeId) {
    return ApiResponse.<PromoCodeResponse>builder()
        .result(promoCodeService.getPromoCodeById(promoCodeId))
        .build();
  }

  // Lấy PromoCode theo mã code
  @GetMapping("/code/{code}")
  public ApiResponse<PromoCodeResponse> getPromoCodeByCode(@PathVariable String code) {
    return ApiResponse.<PromoCodeResponse>builder()
        .result(promoCodeService.getPromoCodeByCode(code))
        .build();
  }

  // Lấy danh sách tất cả PromoCode
  @GetMapping
  public ApiResponse<List<PromoCodeResponse>> getAllPromoCodes(
      @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int pageSize) {
    return ApiResponse.<List<PromoCodeResponse>>builder()
        .result(promoCodeService.getAllPromoCodes())
        .build();
  }
}
