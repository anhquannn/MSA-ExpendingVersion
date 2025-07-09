package com.market.MSA.mappers.product;

import com.market.MSA.models.product.CheckedHistory;
import com.market.MSA.requests.product.CheckedHistoryRequest;
import com.market.MSA.responses.product.CheckedHistoryResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.stereotype.Component;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
@Component
public interface CheckedHistoryMapper {
  CheckedHistory toCheckedHistory(CheckedHistoryRequest checkedHistoryRequest);

  @Mapping(target = "checkedHistoryId", ignore = true)
  CheckedHistoryResponse toCheckedHistoryResponse(CheckedHistory checkedHistory);

  void updateCheckedHistory(
      CheckedHistoryRequest checkedHistoryRequest, @MappingTarget CheckedHistory checkedHistory);
}
