package com.market.MSA.mappers.order;

import com.market.MSA.models.order.ReturnOrder;
import com.market.MSA.requests.order.ReturnOrderRequest;
import com.market.MSA.responses.order.ReturnOrderResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
@Component
public interface ReturnOrderMapper {
  ReturnOrder toReturnOrder(ReturnOrderRequest request);

  ReturnOrderResponse toReturnOrderResponse(ReturnOrder returnOrder);

  @Mapping(target = "returnOrderId", ignore = true)
  void updateReturnOrderFromRequest(
      ReturnOrderRequest request, @MappingTarget ReturnOrder returnOrder);
}
