package com.market.MSA.mappers.others;

import com.market.MSA.models.others.DeliveryInfo;
import com.market.MSA.requests.others.DeliveryInfoRequest;
import com.market.MSA.responses.others.DeliveryInfoResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.stereotype.Component;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
@Component
public interface DeliveryInfoMapper {

  DeliveryInfo toDeliveryInfo(DeliveryInfoRequest request);

  @Mapping(target = "orderId", source = "order.orderId")
  DeliveryInfoResponse toDeliveryInfoResponse(DeliveryInfo deliveryInfo);

  @Mapping(target = "deliveryInfoId", ignore = true)
  void updateDeliveryInfoFromRequest(
      DeliveryInfoRequest request, @MappingTarget DeliveryInfo deliveryInfo);
}
