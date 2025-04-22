package com.market.MSA.mappers.product;

import com.market.MSA.models.product.Category;
import com.market.MSA.requests.product.CategoryRequest;
import com.market.MSA.responses.product.CategoryResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
@Component
public interface CategoryMapper {
  Category toCategory(CategoryRequest request);

  CategoryResponse toCategoryResponse(Category category);

  @Mapping(target = "categoryId", ignore = true)
  void updateCategoryFromRequest(CategoryRequest request, @MappingTarget Category category);
}
