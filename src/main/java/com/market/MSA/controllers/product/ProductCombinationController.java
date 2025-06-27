package com.market.MSA.controllers.product;

import com.market.MSA.constants.ApiMessage;
import com.market.MSA.requests.filters.ProductCombinationFilterRequest;
import com.market.MSA.requests.product.ProductCombinationRequest;
import com.market.MSA.responses.others.ApiResponse;
import com.market.MSA.responses.product.ProductCombinationResponse;
import com.market.MSA.services.product.ProductCombinationService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/product-combinations")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductCombinationController {

  ProductCombinationService pcService;

  @PostMapping
  public ApiResponse<ProductCombinationResponse> create(
      @RequestBody ProductCombinationRequest req) {
    return ApiResponse.<ProductCombinationResponse>builder()
        .result(pcService.create(req))
        .message(ApiMessage.PRODUCT_COMBINATION_CREATED.getMessage())
        .build();
  }

  @PutMapping("/{id}")
  public ApiResponse<ProductCombinationResponse> update(
      @PathVariable Long id, @RequestBody ProductCombinationRequest req) {
    return ApiResponse.<ProductCombinationResponse>builder()
        .result(pcService.update(id, req))
        .message(ApiMessage.PRODUCT_COMBINATION_UPDATED.getMessage())
        .build();
  }

  @DeleteMapping("/{id}")
  public ApiResponse<Boolean> delete(@PathVariable Long id) {
    return ApiResponse.<Boolean>builder()
        .result(pcService.delete(id))
        .message(ApiMessage.PRODUCT_COMBINATION_DELETED.getMessage())
        .build();
  }

  @GetMapping("/{id}")
  public ApiResponse<ProductCombinationResponse> getById(@PathVariable Long id) {
    return ApiResponse.<ProductCombinationResponse>builder()
        .result(pcService.getById(id))
        .message(ApiMessage.PRODUCT_COMBINATION_RETRIEVED.getMessage())
        .build();
  }

  @PostMapping("/list")
  public ApiResponse<List<ProductCombinationResponse>> filter(
      @Valid @RequestBody ProductCombinationFilterRequest req) {
    return ApiResponse.<List<ProductCombinationResponse>>builder()
        .result(pcService.filter(req))
        .message(ApiMessage.ALL_PRODUCT_COMBINATIONS_RETRIEVED.getMessage())
        .build();
  }

  @PostMapping("/paging")
  public ApiResponse<Page<ProductCombinationResponse>> filterPaging(
      @Valid @RequestBody ProductCombinationFilterRequest req) {
    return ApiResponse.<Page<ProductCombinationResponse>>builder()
        .result(pcService.filterPaging(req))
        .message(ApiMessage.ALL_PRODUCT_COMBINATIONS_RETRIEVED.getMessage())
        .build();
  }
}
