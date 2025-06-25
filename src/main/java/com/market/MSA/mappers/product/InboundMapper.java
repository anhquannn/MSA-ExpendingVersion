package com.market.MSA.mappers.product;

import com.market.MSA.models.product.InboundTransfer;
import com.market.MSA.requests.product.InboundRequest;
import com.market.MSA.responses.product.InboundResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
@Component
public interface InboundMapper {
  InboundTransfer toInbound(InboundRequest inboundRequest);

  InboundResponse toInboundResponse(InboundTransfer inboundTransfer);

  @Mapping(target = "inboundTransferId", ignore = true)
  void updateInbound(InboundRequest inboundRequest, @MappingTarget InboundTransfer inboundTransfer);
}
