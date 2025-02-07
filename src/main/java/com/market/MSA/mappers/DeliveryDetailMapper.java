package com.market.MSA.mappers;

import com.market.MSA.models.DeliveryDetail;
import com.market.MSA.requests.DeliveryDetailRequest;
import com.market.MSA.responses.DeliveryDetailResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface DeliveryDetailMapper {
  DeliveryDetail toDeliveryDetail(DeliveryDetailRequest request);

  DeliveryDetailResponse toDeliveryDetailResponse(DeliveryDetail deliveryDetail);

  @Mapping(target = "deliveryDetailId", ignore = true)
  void updateDeliveryDetailFromRequest(
      DeliveryDetailRequest request, @MappingTarget DeliveryDetail deliveryDetail);
}
