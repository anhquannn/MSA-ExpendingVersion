package com.market.MSA.controllers.product;

import com.market.MSA.constants.ApiMessage;
import com.market.MSA.requests.filters.SupplierFilterRequest;
import com.market.MSA.requests.product.SupplierRequest;
import com.market.MSA.responses.others.ApiResponse;
import com.market.MSA.responses.product.ProductImageResponse;
import com.market.MSA.responses.product.SupplierResponse;
import com.market.MSA.services.product.SupplierService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/supplier")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SupplierController {
  SupplierService supplierService;

  @PostMapping
  public ApiResponse<SupplierResponse> createSupplier(@RequestBody SupplierRequest request) {
    return ApiResponse.<SupplierResponse>builder()
        .result(supplierService.createSupplier(request))
        .message(ApiMessage.SUPPLIER_CREATED.getMessage())
        .build();
  }

  @PutMapping("/{id}")
  public ApiResponse<SupplierResponse> updateSupplier(
      @PathVariable Long id, @RequestBody SupplierRequest request) {
    return ApiResponse.<SupplierResponse>builder()
        .result(supplierService.updateSupplier(id, request))
        .message(ApiMessage.SUPPLIER_UPDATED.getMessage())
        .build();
  }

  @DeleteMapping("/{id}")
  public ApiResponse<Boolean> deleteSupplier(@PathVariable Long id) {
    Boolean result = supplierService.deleteSupplier(id);
    return ApiResponse.<Boolean>builder()
        .result(result)
        .message(ApiMessage.SUPPLIER_DELETED.getMessage())
        .build();
  }

  @GetMapping("/{id}")
  public ApiResponse<SupplierResponse> getSupplierById(@PathVariable Long id) {
    return ApiResponse.<SupplierResponse>builder()
        .result(supplierService.getSupplierById(id))
        .message(ApiMessage.SUPPLIER_RETRIEVED.getMessage())
        .build();
  }

  @GetMapping
  public ApiResponse<List<SupplierResponse>> getAll() {
    return ApiResponse.<List<SupplierResponse>>builder()
            .result(supplierService.getAll())
            .message(ApiMessage.ALL_SUPPLIERS_RETRIEVED.getMessage())
            .build();
  }

  @PostMapping("/list")
  public ApiResponse<List<SupplierResponse>> getAllSuppliers(@RequestBody @Valid SupplierFilterRequest request) {
    return ApiResponse.<List<SupplierResponse>>builder()
        .result(supplierService.getAllSuppliers(request))
        .message(ApiMessage.ALL_SUPPLIERS_RETRIEVED.getMessage())
        .build();
  }

  @PostMapping("/paging")
  public ApiResponse<Page<SupplierResponse>> getAllSuppliersWithPaging(
          @RequestBody @Valid SupplierFilterRequest request) {
    return ApiResponse.<Page<SupplierResponse>>builder()
        .result(supplierService.getAllSuppliersWithPaging(request))
        .message(ApiMessage.ALL_SUPPLIERS_RETRIEVED.getMessage())
        .build();
  }
}
