package com.market.MSA.controllers;

import com.market.MSA.requests.ProductRequest;
import com.market.MSA.responses.ApiResponse;
import com.market.MSA.responses.ProductResponse;
import com.market.MSA.services.ProductService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
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
@RequestMapping("/product")
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductController {

  ProductService productService;

  @PostMapping
  public ApiResponse<ProductResponse> createProduct(@RequestBody @Valid ProductRequest request) {
    return ApiResponse.<ProductResponse>builder()
        .result(productService.createProduct(request))
        .build();
  }

  @PutMapping("/{productId}")
  public ApiResponse<ProductResponse> updateProduct(
      @PathVariable long productId, @RequestBody @Valid ProductRequest request) {
    return ApiResponse.<ProductResponse>builder()
        .result(productService.updateProduct(productId, request))
        .build();
  }

  @DeleteMapping("/{productId}")
  public ApiResponse<String> deleteProduct(@PathVariable long productId) {
    productService.deleteProduct(productId);
    return ApiResponse.<String>builder().result("Product has been deleted").build();
  }

  @GetMapping("/{productId}")
  public ApiResponse<ProductResponse> getProductById(@PathVariable long productId) {
    return ApiResponse.<ProductResponse>builder()
        .result(productService.getProductById(productId))
        .build();
  }

  @GetMapping
  public ApiResponse<List<ProductResponse>> getAllProducts(
      @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int pageSize) {
    return ApiResponse.<List<ProductResponse>>builder()
        .result(productService.getAllProducts(page, pageSize))
        .build();
  }

  @GetMapping("/search")
  public ApiResponse<List<ProductResponse>> searchProducts(
      @RequestParam String name,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int pageSize) {
    return ApiResponse.<List<ProductResponse>>builder()
        .result(productService.searchProductsByName(name, page, pageSize))
        .build();
  }

  @GetMapping("/filter")
  public ApiResponse<List<ProductResponse>> filterAndSortProducts(
      @RequestParam(required = false) Integer size,
      @RequestParam(required = false) Double minPrice,
      @RequestParam(required = false) Double maxPrice,
      @RequestParam(required = false) String color,
      @RequestParam(required = false) Long categoryId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int pageSize) {
    return ApiResponse.<List<ProductResponse>>builder()
        .result(
            productService.filterAndSortProducts(
                size, minPrice, maxPrice, color, categoryId, page, pageSize))
        .build();
  }
}
