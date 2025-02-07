package com.market.MSA.mappers;

import com.market.MSA.models.PromoCode;
import com.market.MSA.requests.PromoCodeRequest;
import com.market.MSA.responses.PromoCodeResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PromoCodeMapper {
  PromoCode toPromoCode(PromoCodeRequest request);

  PromoCodeResponse toPromoCodeResponse(PromoCode promoCode);

  @Mapping(target = "promoCodeId", ignore = true)
  void updatePromoCodeFromRequest(PromoCodeRequest request, @MappingTarget PromoCode promoCode);
}
