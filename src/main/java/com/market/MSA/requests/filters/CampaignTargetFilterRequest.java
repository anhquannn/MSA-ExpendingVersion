package com.market.MSA.requests.filters;

import com.market.MSA.constants.PromoScopeType;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CampaignTargetFilterRequest {
  Long campaignId;
  PromoScopeType targetType;
  Long targetId;

  @Builder.Default Integer page = 1;

  @Builder.Default Integer pageSize = 10;

  @Builder.Default String sortBy = "campaignTargetId";

  @Builder.Default String sortDirection = "DESC";
}
