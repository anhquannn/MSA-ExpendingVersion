package com.market.MSA.mappers.product;

import com.market.MSA.models.product.TransferItem;
import com.market.MSA.requests.product.TransferRequestItem;
import com.market.MSA.responses.product.TransferResponseItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.stereotype.Component;

@Mapper(
    componentModel = "spring",
    uses = {ProductMapper.class, TransferRequestMapper.class},
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
@Component
public interface TransferRequestItemMapper {
  TransferItem toTransferRequestItem(TransferRequestItem transferRequestItemItem);

  @Mapping(target = "productResponse", source = "product")
  @Mapping(target = "transferResponse", source = "transfer")
  @Mapping(target = "transferRequestItemId", source = "transferRequestItemId")
  TransferResponseItem toTransferResponseItem(TransferItem transferItem);

  @Mapping(target = "transferRequestItemId", ignore = true)
  void updateTransferRequestItem(
      TransferRequestItem transferRequestItem, @MappingTarget TransferItem transferItem);
}
