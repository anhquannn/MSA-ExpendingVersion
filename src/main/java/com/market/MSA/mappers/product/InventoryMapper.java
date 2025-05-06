package com.market.MSA.mappers.product;

import com.market.MSA.models.product.Inventory;
import com.market.MSA.requests.product.InventoryRequest;
import com.market.MSA.responses.product.InventoryResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.stereotype.Component;

@Mapper(
    componentModel = "spring",
    uses = {BranchMapper.class})
@Component
public interface InventoryMapper {
  Inventory toInventory(InventoryRequest request);

  @Mapping(target = "inventoryProductResponses", ignore = true)
  @Mapping(target = "branch.inventory", ignore = true)
  InventoryResponse toInventoryResponse(Inventory inventory);

  @Mapping(target = "inventoryId", ignore = true)
  void updateInventory(InventoryRequest request, @MappingTarget Inventory inventory);
}
