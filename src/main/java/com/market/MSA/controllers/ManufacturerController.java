package com.market.MSA.controllers;

import com.market.MSA.requests.ManufacturerRequest;
import com.market.MSA.responses.ApiResponse;
import com.market.MSA.responses.ManufacturerResponse;
import com.market.MSA.services.ManufacturerService;
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
        .build();
  }

  @PutMapping("/{id}")
  public ApiResponse<ManufacturerResponse> updateManufacturer(
      @PathVariable Long id, @RequestBody ManufacturerRequest request) {
    return ApiResponse.<ManufacturerResponse>builder()
        .result(manufacturerService.updateManufacturer(id, request))
        .build();
  }

  @DeleteMapping("/{id}")
  public ApiResponse<String> deleteManufacturer(@PathVariable Long id) {
    manufacturerService.deleteManufacturer(id);
    return ApiResponse.<String>builder().result("Manufacturer has been deleted").build();
  }

  @GetMapping("/{id}")
  public ApiResponse<ManufacturerResponse> getManufacturerById(@PathVariable Long id) {
    return ApiResponse.<ManufacturerResponse>builder()
        .result(manufacturerService.getManufacturerById(id))
        .build();
  }

  @GetMapping("/by-name")
  public ApiResponse<ManufacturerResponse> getManufacturerByName(@RequestParam String name) {
    return ApiResponse.<ManufacturerResponse>builder()
        .result(manufacturerService.getManufacturerByName(name))
        .build();
  }

  @GetMapping
  public ApiResponse<List<ManufacturerResponse>> getAllManufacturers() {
    return ApiResponse.<List<ManufacturerResponse>>builder()
        .result(manufacturerService.getAllManufacturers())
        .build();
  }
}
