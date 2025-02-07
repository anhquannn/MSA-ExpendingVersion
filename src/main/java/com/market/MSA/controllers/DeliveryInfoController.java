package com.market.MSA.controllers;

import com.market.MSA.requests.DeliveryInfoRequest;
import com.market.MSA.responses.ApiResponse;
import com.market.MSA.responses.DeliveryInfoResponse;
import com.market.MSA.services.DeliveryInfoService;
import jakarta.validation.Valid;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/deliveryinfo")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DeliveryInfoController {
  DeliveryInfoService deliveryInfoService;

  @PostMapping
  public ApiResponse<DeliveryInfoResponse> createDeliveryInfo(
      @RequestBody @Valid DeliveryInfoRequest request) {
    DeliveryInfoResponse createdDeliveryInfo = deliveryInfoService.createDeliveryInfo(request);
    return ApiResponse.<DeliveryInfoResponse>builder().result(createdDeliveryInfo).build();
  }

  @PutMapping("/{id}")
  public ApiResponse<DeliveryInfoResponse> updateDeliveryInfo(
      @PathVariable Long id, @RequestBody @Valid DeliveryInfoRequest request) {
    DeliveryInfoResponse updatedDeliveryInfo = deliveryInfoService.updateDeliveryInfo(id, request);
    return ApiResponse.<DeliveryInfoResponse>builder().result(updatedDeliveryInfo).build();
  }

  @DeleteMapping("/{id}")
  public ApiResponse<Void> deleteDeliveryInfo(@PathVariable Long id) {
    deliveryInfoService.deleteDeliveryInfo(id);
    return ApiResponse.<Void>builder().result(null).build();
  }

  @GetMapping("/{id}")
  public ApiResponse<DeliveryInfoResponse> getDeliveryInfoById(@PathVariable Long id) {
    DeliveryInfoResponse deliveryInfoResponse = deliveryInfoService.getDeliveryInfoById(id);
    return ApiResponse.<DeliveryInfoResponse>builder().result(deliveryInfoResponse).build();
  }
}
