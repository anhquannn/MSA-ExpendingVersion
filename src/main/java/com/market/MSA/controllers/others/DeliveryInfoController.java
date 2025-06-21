package com.market.MSA.controllers.others;

import com.market.MSA.constants.ApiMessage;
import com.market.MSA.requests.others.DeliveryInfoRequest;
import com.market.MSA.responses.others.ApiResponse;
import com.market.MSA.responses.others.DeliveryInfoResponse;
import com.market.MSA.services.others.DeliveryInfoService;
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
@RequestMapping("/delivery-info")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DeliveryInfoController {
  DeliveryInfoService deliveryInfoService;

  @PostMapping
  public ApiResponse<DeliveryInfoResponse> createDeliveryInfo(
      @RequestBody @Valid DeliveryInfoRequest request) {
    return ApiResponse.<DeliveryInfoResponse>builder()
        .result(deliveryInfoService.createDeliveryInfo(request))
        .message(ApiMessage.DELIVERY_INFO_CREATED.getMessage())
        .build();
  }

  @PutMapping("/{id}")
  public ApiResponse<DeliveryInfoResponse> updateDeliveryInfo(
      @PathVariable Long id, @RequestBody @Valid DeliveryInfoRequest request) {
    return ApiResponse.<DeliveryInfoResponse>builder()
        .result(deliveryInfoService.updateDeliveryInfo(id, request))
        .message(ApiMessage.DELIVERY_INFO_UPDATED.getMessage())
        .build();
  }

  @DeleteMapping("/{id}")
  public ApiResponse<Boolean> deleteDeliveryInfo(@PathVariable Long id) {
    Boolean result = deliveryInfoService.deleteDeliveryInfo(id);
    return ApiResponse.<Boolean>builder()
        .result(result)
        .message(ApiMessage.DELIVERY_INFO_DELETED.getMessage())
        .build();
  }

  @GetMapping("/{id}")
  public ApiResponse<DeliveryInfoResponse> getDeliveryInfoById(@PathVariable Long id) {
    return ApiResponse.<DeliveryInfoResponse>builder()
        .result(deliveryInfoService.getDeliveryInfoById(id))
        .message(ApiMessage.DELIVERY_INFO_RETRIEVED.getMessage())
        .build();
  }
}
