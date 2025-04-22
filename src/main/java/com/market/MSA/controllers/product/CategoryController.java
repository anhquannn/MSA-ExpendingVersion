package com.market.MSA.controllers.product;

import com.market.MSA.constants.ApiMessage;
import com.market.MSA.requests.product.CategoryRequest;
import com.market.MSA.responses.others.ApiResponse;
import com.market.MSA.responses.product.CategoryResponse;
import com.market.MSA.services.product.CategoryService;
import jakarta.validation.Valid;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryController {

  CategoryService categoryService;

  @PostMapping
  public ApiResponse<CategoryResponse> createCategory(@RequestBody @Valid CategoryRequest request) {
    return ApiResponse.<CategoryResponse>builder()
        .result(categoryService.createCategory(request))
        .message(ApiMessage.CATEGORY_CREATED.getMessage())
        .build();
  }

  @PutMapping("/{id}")
  public ApiResponse<CategoryResponse> updateCategory(
      @PathVariable Long id, @RequestBody @Valid CategoryRequest request) {
    return ApiResponse.<CategoryResponse>builder()
        .result(categoryService.updateCategory(id, request))
        .message(ApiMessage.CATEGORY_UPDATED.getMessage())
        .build();
  }

  @DeleteMapping("/{id}")
  public ApiResponse<Boolean> deleteCategory(@PathVariable Long id) {
    Boolean result = categoryService.deleteCategory(id);
    return ApiResponse.<Boolean>builder()
        .result(result)
        .message(ApiMessage.CATEGORY_DELETED.getMessage())
        .build();
  }

  @GetMapping("/{id}")
  public ApiResponse<CategoryResponse> getCategoryById(@PathVariable Long id) {
    return ApiResponse.<CategoryResponse>builder()
        .result(categoryService.getCategoryById(id))
        .message(ApiMessage.CATEGORY_RETRIEVED.getMessage())
        .build();
  }

  @GetMapping
  public ApiResponse<List<CategoryResponse>> getAllCategories() {
    return ApiResponse.<List<CategoryResponse>>builder()
        .result(categoryService.getAllCategories())
        .message(ApiMessage.ALL_CATEGORIES_RETRIEVED.getMessage())
        .build();
  }
}
