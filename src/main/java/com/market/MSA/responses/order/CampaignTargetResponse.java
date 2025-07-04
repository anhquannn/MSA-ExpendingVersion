package com.market.MSA.responses.order;

import com.market.MSA.constants.PromoScopeType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CampaignTargetResponse {
    private Long id;
    private PromoScopeType targetType;
    private Long targetId;
    private Long campaignId;
}
