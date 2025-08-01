package com.market.MSA.mappers.order;

import com.market.MSA.mappers.user.UserMapper;
import com.market.MSA.models.order.ReturnOrder;
import com.market.MSA.requests.order.ReturnOrderRequest;
import com.market.MSA.responses.order.ReturnOrderResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.stereotype.Component;

@Mapper(
    componentModel = "spring",
    uses = {OrderMapper.class, UserMapper.class, ReturnOrderItemMapper.class},
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
@Component
public interface ReturnOrderMapper {

  @Mapping(source = "orderId", target = "order.orderId")
  @Mapping(source = "userId", target = "user.userId")
  @Mapping(target = "returnOrderId", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  ReturnOrder toReturnOrder(ReturnOrderRequest request);

  @Mapping(target = "returnOrderItems.returnOrder", ignore = true)
  ReturnOrderResponse toReturnOrderResponse(ReturnOrder returnOrder);

  @Mapping(target = "returnOrderId", ignore = true)
  @Mapping(target = "order", ignore = true)
  @Mapping(target = "user", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  void updateReturnOrderFromRequest(
      ReturnOrderRequest request, @MappingTarget ReturnOrder returnOrder);
}
