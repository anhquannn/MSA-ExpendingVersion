package com.market.MSA.mappers.product;

import com.market.MSA.models.product.Product;
import com.market.MSA.requests.product.ProductRequest;
import com.market.MSA.responses.product.ProductResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
@Component
public interface ProductMapper {
  Product toProduct(ProductRequest request);

  @Mapping(target = "orderDetails", ignore = true)
  ProductResponse toProductResponse(Product product);

  // Cập nhật sản phẩm từ request
  @Mapping(target = "productId", ignore = true)
  void updateProductFromRequest(ProductRequest request, @MappingTarget Product product);
}
