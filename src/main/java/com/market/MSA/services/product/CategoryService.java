package com.market.MSA.services.product;

import com.market.MSA.exceptions.AppException;
import com.market.MSA.exceptions.ErrorCode;
import com.market.MSA.mappers.product.CategoryMapper;
import com.market.MSA.models.product.Category;
import com.market.MSA.repositories.product.CategoryRepository;
import com.market.MSA.requests.filters.CategoryFilterRequest;
import com.market.MSA.requests.product.CategoryRequest;
import com.market.MSA.responses.others.PaymentResponse;
import com.market.MSA.responses.product.CategoryResponse;
import com.market.MSA.services.others.EntityFinderService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryService {
  final EntityFinderService entityFinderService;

  final CategoryRepository categoryRepository;
  final CategoryMapper categoryMapper;

  @Transactional
  public CategoryResponse createCategory(CategoryRequest request) {
    Category category = categoryMapper.toCategory(request);

    if (request.getParentCategoryId() != null) {
      category.setParentCategory(
          entityFinderService.findByIdOrThrow(
              categoryRepository,
              request.getParentCategoryId(),
              ErrorCode.PARENT_CATEGORY_NOT_FOUND));
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
          entityFinderService.findByIdOrThrow(
              categoryRepository,
              request.getParentCategoryId(),
              ErrorCode.PARENT_CATEGORY_NOT_FOUND));
    }

    categoryMapper.updateCategoryFromRequest(request, category);

    Category categoryUpdate = categoryRepository.save(category);
    return categoryMapper.toCategoryResponse(categoryUpdate);
  }

  @Transactional
  public boolean deleteCategory(Long categoryId) {
    if (!categoryRepository.existsById(categoryId)) {
      throw new AppException(ErrorCode.CATEGORY_NOT_FOUND);
    }
    categoryRepository.deleteById(categoryId);
    return true;
  }

  public CategoryResponse getCategoryById(Long categoryId) {
    Category category =
        categoryRepository
            .findById(categoryId)
            .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
    return categoryMapper.toCategoryResponse(category);
  }

  @Cacheable("all_categories")
  public List<CategoryResponse> getAll() {
    return categoryRepository.findAll().stream().map(categoryMapper::toCategoryResponse).collect(Collectors.toList());
  }

  @Cacheable("categories_list")
  public List<CategoryResponse> getAllCategories(CategoryFilterRequest request) {
    return categoryRepository.filter(request.getName(), request.getParentId()).stream()
        .map(categoryMapper::toCategoryResponse)
        .collect(Collectors.toList());
  }

  @Cacheable("categories_paging")
  public Page<CategoryResponse> getAllCategoriesWithPaging(CategoryFilterRequest request) {
    Sort sort = Sort.by(Sort.Direction.fromString(request.getSortDirection()), request.getSortBy());

    Pageable pageable = PageRequest.of(request.getPage() - 1, request.getPageSize(), sort);
    return categoryRepository
        .filterWithPaging(request.getName(), request.getParentId(), pageable)
        .map(categoryMapper::toCategoryResponse);
  }
}
