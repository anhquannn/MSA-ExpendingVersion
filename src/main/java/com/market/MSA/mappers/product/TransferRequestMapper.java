package com.market.MSA.mappers.product;

import com.market.MSA.mappers.user.UserMapper;
import com.market.MSA.models.product.Transfer;
import com.market.MSA.requests.product.TransferRequest;
import com.market.MSA.responses.product.TransferResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.stereotype.Component;

@Mapper(
    componentModel = "spring",
    uses = {UserMapper.class, InventoryMapper.class})
@Component
public interface TransferRequestMapper {
  Transfer toTransferRequest(com.market.MSA.requests.product.TransferRequest transferRequest);

  @Mapping(target = "requesterResponse", source = "requester")
  @Mapping(target = "approverResponse", source = "approver")
  @Mapping(target = "fromInventoryResponse.branch.inventory", ignore = true)
  @Mapping(target = "toInventoryResponse.branch.inventory", ignore = true)
  @Mapping(target = "fromInventoryResponse", source = "fromInventory")
  @Mapping(target = "toInventoryResponse", source = "toInventory")
  TransferResponse toTransferResponse(Transfer transfer);

  @Mapping(target = "transferRequestId", ignore = true)
  void updateTransferRequest(TransferRequest transferRequest, @MappingTarget Transfer transfer);
}
