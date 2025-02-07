package com.market.MSA.mappers;

import com.market.MSA.models.DeliveryInfo;
import com.market.MSA.requests.DeliveryInfoRequest;
import com.market.MSA.responses.DeliveryInfoResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface DeliveryInfoMapper {
  DeliveryInfo toDeliveryInfo(DeliveryInfoRequest request);

  DeliveryInfoResponse toDeliveryInfoResponse(DeliveryInfo deliveryInfo);

  @Mapping(target = "deliveryInfoId", ignore = true)
  void updateDeliveryInfoFromRequest(
      DeliveryInfoRequest request, @MappingTarget DeliveryInfo deliveryInfo);
}
