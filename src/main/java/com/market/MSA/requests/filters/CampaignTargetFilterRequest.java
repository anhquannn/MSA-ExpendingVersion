package com.market.MSA.requests.filters;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CampaignTargetFilterRequest {
    private Long campaignId;
    private String targetType;
    private Long targetId;
    private Integer page;
    private Integer pageSize;
    private String sortBy;
    private String sortDirection;
}
