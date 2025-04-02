package com.market.MSA.mappers;

import com.market.MSA.models.StockTransfer;
import com.market.MSA.requests.StockTransferRequest;
import com.market.MSA.responses.StockTransferResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
@Component
public interface StockTransferMapper {
  StockTransfer toStockTransfer(StockTransferRequest request);

  StockTransferResponse toStockTransferResponse(StockTransfer stockTransfer);

  @Mapping(target = "stocktransferId", ignore = true)
  void updateStockTransfer(
      StockTransferRequest request, @MappingTarget StockTransfer stockTransfer);
}
