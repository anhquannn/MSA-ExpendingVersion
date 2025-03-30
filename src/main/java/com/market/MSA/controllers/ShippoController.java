package com.market.MSA.controllers;

import com.market.MSA.responses.ApiResponse;
import com.market.MSA.responses.ShippoResponse;
import com.market.MSA.services.ShippoService;
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
public class ShippoController {
  final ShippoService shippoService;

  @PostMapping("/{deliveryId}")
  public ApiResponse<ShippoResponse> createShipment(@PathVariable long deliveryId) {
    return ApiResponse.<ShippoResponse>builder()
        .result(shippoService.createShippo(deliveryId))
        .build();
  }

  @GetMapping("/{objectId}")
  public ApiResponse<ShippoResponse> getShipment(@PathVariable String objectId) {
    return ApiResponse.<ShippoResponse>builder().result(shippoService.getShippo(objectId)).build();
  }

  @GetMapping
  public ApiResponse<List<ShippoResponse>> getShipments() {
    return ApiResponse.<List<ShippoResponse>>builder().result(shippoService.getShippos()).build();
  }
}
