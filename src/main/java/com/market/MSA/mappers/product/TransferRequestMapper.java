package com.market.MSA.mappers.product;

import com.market.MSA.models.product.Transfer;
import com.market.MSA.requests.product.TransferRequest;
import com.market.MSA.responses.product.TransferResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
@Component
public interface TransferRequestMapper {
  Transfer toTransferRequest(com.market.MSA.requests.product.TransferRequest transferRequest);

  TransferResponse toTransferResponse(Transfer transfer);

  @Mapping(target = "transferRequestId", ignore = true)
  void updateTransferRequest(TransferRequest transferRequest, @MappingTarget Transfer transfer);
}
