package com.market.MSA.mappers.order;

import com.market.MSA.models.order.ReturnItemImage;
import com.market.MSA.requests.order.ReturnItemImageRequest;
import com.market.MSA.responses.order.ReturnItemImageResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.stereotype.Component;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
@Component
public interface ReturnItemImageMapper {

  @Mapping(target = "imageId", ignore = true)
  @Mapping(target = "returnOrderItem", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  ReturnItemImage toReturnItemImage(ReturnItemImageRequest request);

  ReturnItemImageResponse toReturnItemImageResponse(ReturnItemImage returnItemImage);

  @Mapping(target = "imageId", ignore = true)
  @Mapping(target = "returnOrderItem", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  void updateReturnItemImageFromRequest(
      ReturnItemImageRequest request, @MappingTarget ReturnItemImage returnItemImage);
}
