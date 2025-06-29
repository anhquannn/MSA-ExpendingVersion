package com.market.MSA.mappers.product;

import com.market.MSA.models.product.ProductCombination;
import com.market.MSA.requests.product.ProductCombinationRequest;
import com.market.MSA.responses.product.ProductCombinationResponse;
import org.mapstruct.*;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
@Component("productCombinationMapper")
public interface ProductCombinationMapper {
  ProductCombination toProductCombination(ProductCombinationRequest request);

  @Mapping(target = "productId1", source = "product1")
  @Mapping(target = "productId2", source = "product2")
  ProductCombinationResponse toProductCombinationResponse(ProductCombination pc);

  @Mapping(target = "combinationId", ignore = true)
  void updateProductCombinationFromRequest(
      ProductCombinationRequest request, @MappingTarget ProductCombination productCombination);
}
