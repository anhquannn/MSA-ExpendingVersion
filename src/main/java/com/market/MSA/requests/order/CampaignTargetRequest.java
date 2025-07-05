package com.market.MSA.requests.order;

import com.market.MSA.constants.PromoScopeType;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CampaignTargetRequest {
  Long campaignId;
  PromoScopeType targetType;
  Long targetId;
}
