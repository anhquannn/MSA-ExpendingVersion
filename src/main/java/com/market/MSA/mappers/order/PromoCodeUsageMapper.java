package com.market.MSA.mappers.order;

import com.market.MSA.models.order.PromoCodeUsage;
import com.market.MSA.requests.order.PromoCodeUsageRequest;
import com.market.MSA.responses.order.PromoCodeUsageResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.stereotype.Component;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
@Component
public interface PromoCodeUsageMapper {
  PromoCodeUsage toUsage(PromoCodeUsageRequest request);

  PromoCodeUsageResponse toResponse(PromoCodeUsage usage);

  @Mapping(target = "promoCodeUsageId", ignore = true)
  void updatePromoCodeUsage(
      PromoCodeUsageRequest promoCodeUsageRequest, @MappingTarget PromoCodeUsage promoCodeUsage);
}
