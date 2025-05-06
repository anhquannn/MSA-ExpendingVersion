package com.market.MSA.mappers.order;

import com.market.MSA.models.order.Order;
import com.market.MSA.requests.order.OrderRequest;
import com.market.MSA.responses.order.OrderResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
@Component
public interface OrderMapper {

  @Mapping(source = "promoCodes", target = "promoCodes", ignore = true)
  Order toOrder(OrderRequest request);

  @Mapping(target = "branch.inventory", ignore = true)
  OrderResponse toOrderResponse(Order order);

  @Mapping(target = "orderId", ignore = true)
  @Mapping(source = "promoCodes", target = "promoCodes", ignore = true)
  void updateOrderFromRequest(OrderRequest request, @MappingTarget Order order);
}
