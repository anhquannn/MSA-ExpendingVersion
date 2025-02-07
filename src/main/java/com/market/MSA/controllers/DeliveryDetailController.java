package com.market.MSA.controllers;

import com.market.MSA.requests.DeliveryDetailRequest;
import com.market.MSA.responses.ApiResponse;
import com.market.MSA.responses.DeliveryDetailResponse;
import com.market.MSA.services.DeliveryDetailService;
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
@RequestMapping("/deliverydetail")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DeliveryDetailController {
  DeliveryDetailService deliveryDetailService;

  @PostMapping
  public ApiResponse<DeliveryDetailResponse> createDeliveryDetail(
      @RequestBody @Valid DeliveryDetailRequest request) {
    DeliveryDetailResponse response = deliveryDetailService.createDeliveryDetail(request);
    return ApiResponse.<DeliveryDetailResponse>builder().result(response).build();
  }

  @PutMapping("/{id}")
  public ApiResponse<DeliveryDetailResponse> updateDeliveryDetail(
      @PathVariable Long id, @RequestBody @Valid DeliveryDetailRequest request) {
    DeliveryDetailResponse response = deliveryDetailService.updateDeliveryDetail(id, request);
    return ApiResponse.<DeliveryDetailResponse>builder().result(response).build();
  }

  @DeleteMapping("/{id}")
  public ApiResponse<Void> deleteDeliveryDetail(@PathVariable Long id) {
    deliveryDetailService.deleteDeliveryDetail(id);
    return ApiResponse.<Void>builder().result(null).build();
  }

  @GetMapping("/{id}")
  public ApiResponse<DeliveryDetailResponse> getDeliveryDetailById(@PathVariable Long id) {
    DeliveryDetailResponse response = deliveryDetailService.getDeliveryDetailById(id);
    return ApiResponse.<DeliveryDetailResponse>builder().result(response).build();
  }

  @GetMapping
  public ApiResponse<List<DeliveryDetailResponse>> getAllDeliveryDetails(
      @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int pageSize) {
    List<DeliveryDetailResponse> responses =
        deliveryDetailService.getAllDeliveryDetails(page, pageSize);
    return ApiResponse.<List<DeliveryDetailResponse>>builder().result(responses).build();
  }

  @GetMapping("/delivery-info/{deliveryInfoId}")
  public ApiResponse<List<DeliveryDetailResponse>> getAllDeliveryDetailsByDeliveryInfoId(
      @PathVariable Long deliveryInfoId,
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int pageSize) {
    List<DeliveryDetailResponse> responses =
        deliveryDetailService.getAllDeliveryDetailsByDeliveryInfoId(deliveryInfoId, page, pageSize);
    return ApiResponse.<List<DeliveryDetailResponse>>builder().result(responses).build();
  }
}
