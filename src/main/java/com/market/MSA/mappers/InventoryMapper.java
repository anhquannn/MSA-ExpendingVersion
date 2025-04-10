package com.market.MSA.mappers;

import com.market.MSA.models.Inventory;
import com.market.MSA.requests.InventoryRequest;
import com.market.MSA.responses.InventoryResponse;
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
  InventoryResponse toInventoryResponse(Inventory inventory);

  @Mapping(target = "inventoryId", ignore = true)
  void updateInventory(InventoryRequest request, @MappingTarget Inventory inventory);
}
