package com.market.MSA.mappers.product;

import com.market.MSA.mappers.user.UserMapper;
import com.market.MSA.models.product.InboundTransfer;
import com.market.MSA.requests.product.InboundRequest;
import com.market.MSA.responses.product.InboundResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.stereotype.Component;

@Mapper(
    componentModel = "spring",
    uses = {UserMapper.class, InventoryMapper.class, TransferRequestMapper.class},
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
@Component
public interface InboundMapper {
  InboundTransfer toInbound(InboundRequest inboundRequest);

  @Mapping(target = "userResponse", source = "user")
  @Mapping(target = "inventoryResponse.branch.inventory", ignore = true)
  @Mapping(target = "inventoryResponse", source = "inventory")
  @Mapping(target = "transferResponse", source = "transfer")
  InboundResponse toInboundResponse(InboundTransfer inboundTransfer);

  @Mapping(target = "inboundTransferId", ignore = true)
  void updateInbound(InboundRequest inboundRequest, @MappingTarget InboundTransfer inboundTransfer);
}
