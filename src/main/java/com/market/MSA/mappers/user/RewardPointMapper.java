package com.market.MSA.mappers.user;

import com.market.MSA.models.user.RewardPoint;
import com.market.MSA.requests.user.RewardPointRequest;
import com.market.MSA.responses.user.RewardPointResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.stereotype.Component;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
@Component
public interface RewardPointMapper {
  RewardPoint toRewardPoint(RewardPointRequest request);

  RewardPointResponse toRewardPointResponse(RewardPoint rewardPoint);

  @Mapping(target = "rewardPointId", ignore = true)
  void updateRewardPoint(
      RewardPointRequest rewardPointRequest, @MappingTarget RewardPoint rewardPoint);
}
