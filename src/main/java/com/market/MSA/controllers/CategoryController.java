package com.market.MSA.controllers;

import com.market.MSA.requests.CategoryRequest;
import com.market.MSA.responses.ApiResponse;
import com.market.MSA.responses.CategoryResponse;
import com.market.MSA.services.CategoryService;
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
  public ApiResponse<CategoryResponse> createCategory(@RequestBody CategoryRequest request) {
    return ApiResponse.<CategoryResponse>builder()
        .result(categoryService.createCategory(request))
        .build();
  }

  @PutMapping("/{id}")
  public ApiResponse<CategoryResponse> updateCategory(
      @PathVariable Long id, @RequestBody CategoryRequest request) {
    return ApiResponse.<CategoryResponse>builder()
        .result(categoryService.updateCategory(id, request))
        .build();
  }

  @DeleteMapping("/{id}")
  public ApiResponse<String> deleteCategory(@PathVariable Long id) {
    categoryService.deleteCategory(id);
    return ApiResponse.<String>builder().result("Category has been deleted").build();
  }

  @GetMapping("/{id}")
  public ApiResponse<CategoryResponse> getCategoryById(@PathVariable Long id) {
    return ApiResponse.<CategoryResponse>builder()
        .result(categoryService.getCategoryById(id))
        .build();
  }

  @GetMapping
  public ApiResponse<List<CategoryResponse>> getAllCategories() {
    return ApiResponse.<List<CategoryResponse>>builder()
        .result(categoryService.getAllCategories())
        .build();
  }
}
