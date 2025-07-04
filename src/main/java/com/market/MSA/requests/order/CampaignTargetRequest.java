package com.market.MSA.requests.order;

import com.market.MSA.constants.PromoScopeType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CampaignTargetRequest {
    private Long campaignId;
    private PromoScopeType targetType;
    private Long targetId;
}
