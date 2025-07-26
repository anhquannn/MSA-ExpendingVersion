package com.market.MSA.mappers.product;

import com.market.MSA.models.product.Promotion;
import com.market.MSA.requests.product.PromotionRequest;
import com.market.MSA.responses.product.PromotionResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.stereotype.Component;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
@Component
public interface PromotionMapper {
  Promotion toPromotion(PromotionRequest promotionRequest);

  @Mapping(target = "productMain", source = "productMain")
  @Mapping(target = "productFree", source = "productFree")
  @Mapping(target = "productMain.productImageResponses", source = "productMain.images")
  @Mapping(target = "productFree.productImageResponses", source = "productFree.images")
  PromotionResponse toPromotionResponse(Promotion promotion);

  @Mapping(target = "promotionId", ignore = true)
  void updatePromotionFromRequest(PromotionRequest request, @MappingTarget Promotion promotion);
}
