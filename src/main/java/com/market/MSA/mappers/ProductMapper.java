package com.market.MSA.mappers;

import com.market.MSA.models.Product;
import com.market.MSA.requests.ProductRequest;
import com.market.MSA.responses.ProductResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
@Component
public interface ProductMapper {
  Product toProduct(ProductRequest request);

  ProductResponse toProductResponse(Product product);

  // Cập nhật sản phẩm từ request
  @Mapping(target = "productId", ignore = true)
  void updateProductFromRequest(ProductRequest request, @MappingTarget Product product);
}
