package com.market.MSA.mappers.order;

import com.market.MSA.models.order.CampaignTarget;
import com.market.MSA.requests.order.CampaignTargetRequest;
import com.market.MSA.responses.order.CampaignTargetResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.stereotype.Component;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
@Component
public interface CampaignTargetMapper {
  CampaignTarget toCampaignTarget(CampaignTargetRequest campaignTargetRequest);

  CampaignTargetResponse toCampaignTargetResponse(CampaignTarget campaignTarget);

  @Mapping(target = "campaignTargetId", ignore = true)
  void updateCampaignTarget(
      CampaignTargetRequest campaignTargetRequest, @MappingTarget CampaignTarget campaignTarget);
}
