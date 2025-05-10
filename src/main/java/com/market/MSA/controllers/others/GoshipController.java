package com.market.MSA.controllers.others;

import com.market.MSA.constants.ApiMessage;
import com.market.MSA.requests.goship.RatesRequest;
import com.market.MSA.requests.goship.ShipmentRequest;
import com.market.MSA.responses.goship.*;
import com.market.MSA.responses.others.ApiResponse;
import com.market.MSA.services.others.GoshipService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/shipment")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GoshipController {

  GoshipService goshipService;

  @GetMapping("/cities")
  public ApiResponse<List<CityResponse>> getCities() {
    return ApiResponse.<List<CityResponse>>builder()
        .result(goshipService.getCities())
        .message(ApiMessage.CITIES_RETRIEVED.getMessage())
        .build();
  }

  @GetMapping("/districts/{cityCode}")
  public ApiResponse<List<DistrictResponse>> getDistricts(@PathVariable int cityCode) {
    return ApiResponse.<List<DistrictResponse>>builder()
        .result(goshipService.getDistricts(cityCode))
        .message(ApiMessage.DISTRICTS_RETRIEVED.getMessage())
        .build();
  }

  @GetMapping("/wards/{districtCode}")
  public ApiResponse<List<WardResponse>> getWards(@PathVariable int districtCode) {
    return ApiResponse.<List<WardResponse>>builder()
        .result(goshipService.getWards(districtCode))
        .message(ApiMessage.WARDS_RETRIEVED.getMessage())
        .build();
  }

  @PostMapping("/rates")
  public ApiResponse<List<RatesResponse>> createRates(@RequestBody @Valid RatesRequest request) {
    return ApiResponse.<List<RatesResponse>>builder()
        .result(goshipService.createRates(request))
        .message(ApiMessage.RATES_CREATED.getMessage())
        .build();
  }

  @PostMapping("/{orderId}")
  public ApiResponse<ShipmentResponse> createShipment(
      @PathVariable Long orderId, @RequestBody @Valid ShipmentRequest request) {
    return ApiResponse.<ShipmentResponse>builder()
        .result(goshipService.createShipment(request, orderId))
        .message(ApiMessage.SHIPMENT_CREATED.getMessage())
        .build();
  }

  @GetMapping
  public ApiResponse<List<ShipmentDetailResponse>> getAllShipments() {
    return ApiResponse.<List<ShipmentDetailResponse>>builder()
        .result(goshipService.getAllShipments())
        .message(ApiMessage.ALL_SHIPMENTS_RETRIEVED.getMessage())
        .build();
  }

  @GetMapping("/search")
  public ApiResponse<List<ShipmentDetailResponse>> searchShipmentsByCode(
      @RequestParam String code) {
    return ApiResponse.<List<ShipmentDetailResponse>>builder()
        .result(goshipService.searchShipmentsByCode(code))
        .message(ApiMessage.ALL_SHIPMENTS_RETRIEVED.getMessage())
        .build();
  }

  @GetMapping("/time-range")
  public ApiResponse<List<ShipmentDetailResponse>> searchShipmentsByTimeRange(
      @RequestParam(required = false) Integer from, @RequestParam(required = false) Integer to) {
    return ApiResponse.<List<ShipmentDetailResponse>>builder()
        .result(goshipService.searchShipmentsByTimeRange(from, to))
        .message(ApiMessage.ALL_SHIPMENTS_RETRIEVED.getMessage())
        .build();
  }
}
