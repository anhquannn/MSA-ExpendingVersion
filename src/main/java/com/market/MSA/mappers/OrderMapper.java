package com.market.MSA.mappers;

import com.market.MSA.models.Order;
import com.market.MSA.requests.OrderRequest;
import com.market.MSA.responses.OrderResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
@Component
public interface OrderMapper {

  @Mapping(source = "promoCodes", target = "promoCodes", ignore = true)
  Order toOrder(OrderRequest request);

  @Mapping(source = "promoCodes", target = "promoCodes", ignore = true)
  OrderResponse toOrderResponse(Order order);

  @Mapping(target = "orderId", ignore = true)
  @Mapping(source = "promoCodes", target = "promoCodes", ignore = true)
  void updateOrderFromRequest(OrderRequest request, @MappingTarget Order order);
}
