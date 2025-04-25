package com.market.MSA.mappers.product;

import com.market.MSA.models.product.TransferRequest;
import com.market.MSA.responses.product.TransferResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
@Component
public interface TransferRequestMapper {
  TransferRequest toTransferRequest(TransferRequest transferRequest);

  TransferResponse toTransferResponse(TransferResponse transferResponse);

  @Mapping(target = "transferRequestId", ignore = true)
  void updateTransferRequest(
      TransferRequest transferRequest, @MappingTarget TransferRequest transfer);
}
