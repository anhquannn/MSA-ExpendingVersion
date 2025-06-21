package com.market.MSA.mappers.order;

import com.market.MSA.models.order.CancelOrder;
import com.market.MSA.requests.order.CancelOrderRequest;
import com.market.MSA.responses.order.CancelOrderResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
@Component
public interface CancelOrderMapper {
  CancelOrder toCancelOrder(CancelOrderRequest request);

  CancelOrderResponse toCancelOrderResponse(CancelOrder cancelOrder);

  @Mapping(target = "cancelOrderId", ignore = true)
  void updateCancelOrderFromRequest(
      CancelOrderRequest request, @MappingTarget CancelOrder cancelOrder);
}
