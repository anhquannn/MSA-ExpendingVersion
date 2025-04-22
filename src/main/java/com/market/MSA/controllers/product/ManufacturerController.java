package com.market.MSA.controllers.product;

import com.market.MSA.constants.ApiMessage;
import com.market.MSA.requests.product.ManufacturerRequest;
import com.market.MSA.responses.others.ApiResponse;
import com.market.MSA.responses.product.ManufacturerResponse;
import com.market.MSA.services.product.ManufacturerService;
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
@RequestMapping("/manufacturer")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ManufacturerController {
  ManufacturerService manufacturerService;

  @PostMapping
  public ApiResponse<ManufacturerResponse> createManufacturer(
      @RequestBody ManufacturerRequest request) {
    return ApiResponse.<ManufacturerResponse>builder()
        .result(manufacturerService.createManufacturer(request))
        .message(ApiMessage.MANUFACTURER_CREATED.getMessage())
        .build();
  }

  @PutMapping("/{id}")
  public ApiResponse<ManufacturerResponse> updateManufacturer(
      @PathVariable Long id, @RequestBody ManufacturerRequest request) {
    return ApiResponse.<ManufacturerResponse>builder()
        .result(manufacturerService.updateManufacturer(id, request))
        .message(ApiMessage.MANUFACTURER_UPDATED.getMessage())
        .build();
  }

  @DeleteMapping("/{id}")
  public ApiResponse<Boolean> deleteManufacturer(@PathVariable Long id) {
    Boolean result = manufacturerService.deleteManufacturer(id);
    return ApiResponse.<Boolean>builder()
        .result(result)
        .message(ApiMessage.MANUFACTURER_DELETED.getMessage())
        .build();
  }

  @GetMapping("/{id}")
  public ApiResponse<ManufacturerResponse> getManufacturerById(@PathVariable Long id) {
    return ApiResponse.<ManufacturerResponse>builder()
        .result(manufacturerService.getManufacturerById(id))
        .message(ApiMessage.MANUFACTURER_RETRIEVED.getMessage())
        .build();
  }

  @GetMapping("/by-name")
  public ApiResponse<ManufacturerResponse> getManufacturerByName(@RequestParam String name) {
    return ApiResponse.<ManufacturerResponse>builder()
        .result(manufacturerService.getManufacturerByName(name))
        .message(ApiMessage.MANUFACTURER_RETRIEVED.getMessage())
        .build();
  }

  @GetMapping
  public ApiResponse<List<ManufacturerResponse>> getAllManufacturers() {
    return ApiResponse.<List<ManufacturerResponse>>builder()
        .result(manufacturerService.getAllManufacturers())
        .message(ApiMessage.ALL_MANUFACTURERS_RETRIEVED.getMessage())
        .build();
  }
}
