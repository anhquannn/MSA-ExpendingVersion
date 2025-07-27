package com.market.MSA.mappers.order;

import com.market.MSA.mappers.user.RoleMapper;
import com.market.MSA.mappers.user.UserAddressMappper;
import com.market.MSA.models.order.OrderDetail;
import com.market.MSA.requests.order.OrderDetailRequest;
import com.market.MSA.responses.order.OrderDetailResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.stereotype.Component;

@Mapper(
    componentModel = "spring",
    uses = {UserAddressMappper.class, RoleMapper.class},
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
@Component
public interface OrderDetailMapper {
  OrderDetail toOrderDetail(OrderDetailRequest request);

  @Mapping(target = "product.productImageResponses", source = "product.images")
  @Mapping(target = "freeItems", ignore = true)
  OrderDetailResponse toOrderDetailResponse(OrderDetail orderDetail);

  @Mapping(target = "orderDetailId", ignore = true)
  void updateOrderDetailFromRequest(
      OrderDetailRequest request, @MappingTarget OrderDetail orderDetail);
}
