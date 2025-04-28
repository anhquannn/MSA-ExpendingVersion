package com.market.MSA.mappers.user;

import com.market.MSA.models.user.RewardPointTransaction;
import com.market.MSA.requests.user.RewardPointTransactionRequest;
import com.market.MSA.responses.user.RewardPointTransactionResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
@Component
public interface RewardPointTransactionMapper {
  RewardPointTransaction toRewardPointTransaction(RewardPointTransactionRequest request);

  RewardPointTransactionResponse toRewardPointTransactionResponse(
      RewardPointTransaction rewardPointTransaction);

  @Mapping(target = "rewardPointTransactionId", ignore = true)
  void updateRewardPointTransaction(
      RewardPointTransactionRequest rewardPointTransactionRequest,
      @MappingTarget RewardPointTransaction rewardPointTransaction);
}
