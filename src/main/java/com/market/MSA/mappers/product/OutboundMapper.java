package com.market.MSA.mappers.product;

import com.market.MSA.models.product.InboundTransfer;
import com.market.MSA.models.product.OutboundTransfer;
import com.market.MSA.requests.product.InboundRequest;
import com.market.MSA.requests.product.OutboundRequest;
import com.market.MSA.responses.product.OutboundResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
@Component
public interface OutboundMapper {
  OutboundTransfer toOutboundTransfer(InboundRequest inboundRequest);

  OutboundResponse toOutboundResponse(InboundTransfer inboundTransfer);

  @Mapping(target = "outboundTransferId", ignore = true)
  void updateOutbound(
      OutboundRequest outboundRequest, @MappingTarget OutboundTransfer outboundTransfer);
}
