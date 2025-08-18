package com.market.MSA.mappers.order;

import com.market.MSA.models.order.ReturnOrderItem;
import com.market.MSA.requests.order.ReturnOrderItemRequest;
import com.market.MSA.responses.order.ReturnOrderItemResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.stereotype.Component;

@Mapper(
    componentModel = "spring",
    uses = {OrderDetailMapper.class, ReturnItemImageMapper.class},
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
@Component
public interface ReturnOrderItemMapper {

  @Mapping(source = "orderDetailId", target = "orderDetail.orderDetailId")
  @Mapping(target = "itemId", ignore = true)
  @Mapping(target = "returnOrder", ignore = true)
  ReturnOrderItem toReturnOrderItem(ReturnOrderItemRequest request);

  @Mapping(target = "images.returnOrderItem", ignore = true)
  ReturnOrderItemResponse toReturnOrderItemResponse(ReturnOrderItem returnOrderItem);

  @Mapping(target = "itemId", ignore = true)
  @Mapping(target = "returnOrder", ignore = true)
  @Mapping(target = "orderDetail", ignore = true)
  void updateReturnOrderItemFromRequest(
      ReturnOrderItemRequest request, @MappingTarget ReturnOrderItem returnOrderItem);
}
