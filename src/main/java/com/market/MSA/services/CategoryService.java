package com.market.MSA.services;

import com.market.MSA.exceptions.AppException;
import com.market.MSA.exceptions.ErrorCode;
import com.market.MSA.mappers.CategoryMapper;
import com.market.MSA.models.Category;
import com.market.MSA.repositories.CategoryRepository;
import com.market.MSA.requests.CategoryRequest;
import com.market.MSA.responses.CategoryResponse;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryService {

  private final CategoryRepository categoryRepository;
  private final CategoryMapper categoryMapper;

  @Transactional
  public CategoryResponse createCategory(CategoryRequest request) {
    Category category = categoryMapper.toCategory(request);

    if (request.getParentCategoryId() != null) {
      category.setParentCategory(
          categoryRepository
              .findById(request.getParentCategoryId())
              .orElseThrow(() -> new AppException(ErrorCode.PARENT_CATEGORY_NOT_FOUND)));
    }

    category = categoryRepository.save(category);
    return categoryMapper.toCategoryResponse(category);
  }

  @Transactional
  public CategoryResponse updateCategory(Long categoryId, CategoryRequest request) {
    Category category =
        categoryRepository
            .findById(categoryId)
            .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
    categoryMapper.updateCategoryFromRequest(request, category);

    if (request.getParentCategoryId() != null) {
      category.setParentCategory(
          categoryRepository
              .findById(request.getParentCategoryId())
              .orElseThrow(() -> new AppException(ErrorCode.PARENT_CATEGORY_NOT_FOUND)));
    }

    category = categoryRepository.save(category);
    return categoryMapper.toCategoryResponse(category);
  }

  @Transactional
  public void deleteCategory(Long categoryId) {
    if (!categoryRepository.existsById(categoryId)) {
      throw new AppException(ErrorCode.CATEGORY_NOT_FOUND);
    }
    categoryRepository.deleteById(categoryId);
  }

  public CategoryResponse getCategoryById(Long categoryId) {
    Category category =
        categoryRepository
            .findById(categoryId)
            .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
    return categoryMapper.toCategoryResponse(category);
  }

  public List<CategoryResponse> getAllCategories() {
    return categoryRepository.findAll().stream()
        .map(categoryMapper::toCategoryResponse)
        .collect(Collectors.toList());
  }
}
