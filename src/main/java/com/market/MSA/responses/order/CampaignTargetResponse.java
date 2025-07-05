package com.market.MSA.responses.order;

import com.market.MSA.constants.PromoScopeType;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CampaignTargetResponse {
  Long campaignTargetId;
  PromoScopeType targetType;
  Long targetId;
  CampaignResponse campaign;
}
