package com.market.MSA.mappers.order;

import com.market.MSA.models.order.Campaign;
import com.market.MSA.requests.order.CampaignRequest;
import com.market.MSA.responses.order.CampaignResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
@Component
public interface CampaignMapper {
  Campaign toCampaign(CampaignRequest request);

  CampaignResponse toCampaignResponse(Campaign campaign);

  @Mapping(target = "campaignId", ignore = true)
  void updateCampaign(@MappingTarget Campaign campaign, CampaignRequest request);
}
