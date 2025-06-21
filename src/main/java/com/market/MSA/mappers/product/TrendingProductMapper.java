package com.market.MSA.mappers.product;

import com.market.MSA.models.product.TrendingProduct;
import com.market.MSA.requests.product.TrendingProductRequest;
import com.market.MSA.responses.product.TrendingProductResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
@Component
public interface TrendingProductMapper {
  TrendingProduct toTrendingProduct(TrendingProductRequest request);

  TrendingProductResponse toTrendingProductResponse(TrendingProduct trendingProduct);

  @Mapping(target = "trendId", ignore = true)
  void updateTrendingProductFromRequest(
      TrendingProductRequest request, @MappingTarget TrendingProduct trendingProduct);
}
