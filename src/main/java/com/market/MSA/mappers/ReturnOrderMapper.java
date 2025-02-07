package com.market.MSA.mappers;

import com.market.MSA.models.ReturnOrder;
import com.market.MSA.requests.ReturnOrderRequest;
import com.market.MSA.responses.ReturnOrderResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ReturnOrderMapper {
  ReturnOrder toReturnOrder(ReturnOrderRequest request);

  ReturnOrderResponse toReturnOrderResponse(ReturnOrder returnOrder);

  @Mapping(target = "returnOrderId", ignore = true)
  void updateReturnOrderFromRequest(
      ReturnOrderRequest request, @MappingTarget ReturnOrder returnOrder);
}
