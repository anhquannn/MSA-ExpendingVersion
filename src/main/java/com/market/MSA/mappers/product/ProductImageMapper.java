package com.market.MSA.mappers.product;

import com.market.MSA.models.product.ProductImage;
import com.market.MSA.requests.product.ProductImageRequest;
import com.market.MSA.responses.product.ProductImageResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
@Component
public interface ProductImageMapper {
  ProductImage toProductImage(ProductImageRequest productImageRequest);

  @Mapping(target = "productResponse", ignore = true) // Prevent circular reference
  ProductImageResponse toProductImageResponse(ProductImage productImage);

  @Mapping(target = "productImageId", ignore = true)
  void updateProductImage(
      ProductImageRequest productImageRequest, @MappingTarget ProductImage productImage);
}
