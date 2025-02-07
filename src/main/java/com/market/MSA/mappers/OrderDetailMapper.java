package com.market.MSA.mappers;

import com.market.MSA.models.OrderDetail;
import com.market.MSA.requests.OrderDetailRequest;
import com.market.MSA.responses.OrderDetailResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface OrderDetailMapper {
  OrderDetail toOrderDetail(OrderDetailRequest request);

  OrderDetailResponse toOrderDetailResponse(OrderDetail orderDetail);

  @Mapping(target = "orderDetailId", ignore = true)
  void updateOrderDetailFromRequest(
      OrderDetailRequest request, @MappingTarget OrderDetail orderDetail);
}
