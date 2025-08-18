package com.market.MSA.mappers.product;

import com.market.MSA.models.product.InventoryCheckRequest;
import com.market.MSA.requests.product.InventoryCheckRequestRequest;
import com.market.MSA.responses.product.InventoryCheckRequestResponse;
import org.mapstruct.*;
import org.springframework.stereotype.Component;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
@Component("inventoryCheckRequestMapper")
public interface InventoryCheckRequestMapper {
  InventoryCheckRequest toInventoryCheckRequest(InventoryCheckRequestRequest request);

  @Mapping(target = "inventoryId", source = "inventory.inventoryId")
  @Mapping(target = "surveyor", source = "surveyor")
  @Mapping(target = "userId", source = "user.userId")
  InventoryCheckRequestResponse toInventoryCheckRequestResponse(InventoryCheckRequest entity);

  @Mapping(target = "icrId", ignore = true)
  void updateInventoryCheckRequest(
      InventoryCheckRequestRequest request, @MappingTarget InventoryCheckRequest entity);
}
