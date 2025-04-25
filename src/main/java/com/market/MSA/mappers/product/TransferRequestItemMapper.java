package com.market.MSA.mappers.product;

import com.market.MSA.models.product.TransferRequestItem;
import com.market.MSA.responses.product.TransferResponseItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
@Component
public interface TransferRequestItemMapper {
  TransferRequestItem toTransferRequestItem(TransferRequestItem transferRequestItem);

  TransferResponseItem toTransferResponseItem(TransferResponseItem transferResponseItem);

  @Mapping(target = "transferRequestItemId", ignore = true)
  void updateTransferRequestItem(
      TransferRequestItem transferRequestItem, @MappingTarget TransferRequestItem transferItem);
}
