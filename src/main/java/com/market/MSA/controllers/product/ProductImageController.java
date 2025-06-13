package com.market.MSA.controllers.product;

import com.market.MSA.constants.ApiMessage;
import com.market.MSA.requests.product.ProductImageRequest;
import com.market.MSA.responses.others.ApiResponse;
import com.market.MSA.responses.product.ProductImageResponse;
import com.market.MSA.services.product.ProductImageService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/image")
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductImageController {
  ProductImageService productImageService;

  @PostMapping
  public ApiResponse<ProductImageResponse> createProductImage(
      @RequestBody @Valid ProductImageRequest request) {
    return ApiResponse.<ProductImageResponse>builder()
        .result(productImageService.createProductImage(request))
        .message(ApiMessage.PRODUCT_IMAGE_CREATED.getMessage())
        .build();
  }

  @PutMapping("/{imageId}")
  public ApiResponse<ProductImageResponse> updateProductImage(
      @PathVariable long imageId, @RequestBody @Valid ProductImageRequest request) {
    return ApiResponse.<ProductImageResponse>builder()
        .result(productImageService.updateProductImage(imageId, request))
        .message(ApiMessage.PRODUCT_IMAGE_UPDATED.getMessage())
        .build();
  }

  @DeleteMapping("/{imageId}")
  public ApiResponse<Boolean> deleteProductImage(@PathVariable long imageId) {
    Boolean result = productImageService.deleteProductImage(imageId);
    return ApiResponse.<Boolean>builder()
        .result(result)
        .message(ApiMessage.PRODUCT_IMAGE_DELETED.getMessage())
        .build();
  }

  @GetMapping("/{imageId}")
  public ApiResponse<ProductImageResponse> getProductImageById(@PathVariable long imageId) {
    return ApiResponse.<ProductImageResponse>builder()
        .result(productImageService.getProductImageById(imageId))
        .message(ApiMessage.PRODUCT_IMAGE_RETRIEVED.getMessage())
        .build();
  }

  @GetMapping
  public ApiResponse<List<ProductImageResponse>> getAllProductImages(
      @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int pageSize) {
    return ApiResponse.<List<ProductImageResponse>>builder()
        .result(productImageService.getAllProductImages(page, pageSize))
        .message(ApiMessage.ALL_PRODUCT_IMAGES_RETRIEVED.getMessage())
        .build();
  }
}
