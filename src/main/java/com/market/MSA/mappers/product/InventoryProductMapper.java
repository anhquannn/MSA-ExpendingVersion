package com.market.MSA.mappers.product;

import com.market.MSA.models.product.InventoryProduct;
import com.market.MSA.requests.product.InventoryProductRequest;
import com.market.MSA.responses.product.InventoryProductResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.stereotype.Component;

@Mapper(
    componentModel = "spring",
    uses = {ProductMapper.class, InventoryMapper.class})
@Component
public interface InventoryProductMapper {
  InventoryProduct toInventoryProduct(InventoryProductRequest request);

  InventoryProductResponse toInventoryProductResponse(InventoryProduct inventoryProduct);

  @Mapping(target = "inventoryProductId", ignore = true)
  void updateInventoryProductFromRequest(
      InventoryProductRequest request, @MappingTarget InventoryProduct inventoryProduct);
}
