package com.market.MSA.mappers;

import com.market.MSA.models.InventoryProduct;
import com.market.MSA.requests.InventoryProductRequest;
import com.market.MSA.responses.InventoryProductResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
@Component
public interface InventoryProductMapper {
  InventoryProduct toInventoryProduct(InventoryProductRequest request);

  InventoryProductResponse toInventoryProductResponse(InventoryProduct inventoryProduct);

  @Mapping(target = "inventoryProductId", ignore = true)
  void updateInventoryProductFromRequest(
      InventoryProductRequest request, @MappingTarget InventoryProduct inventoryProduct);
}
