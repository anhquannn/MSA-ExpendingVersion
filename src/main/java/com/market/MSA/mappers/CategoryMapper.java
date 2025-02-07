package com.market.MSA.mappers;

import com.market.MSA.models.Category;
import com.market.MSA.requests.CategoryRequest;
import com.market.MSA.responses.CategoryResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
  Category toCategory(CategoryRequest request);

  CategoryResponse toCategoryResponse(Category category);

  @Mapping(target = "categoryId", ignore = true)
  void updateCategoryFromRequest(CategoryRequest request, @MappingTarget Category category);
}
