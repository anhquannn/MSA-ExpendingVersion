package com.market.MSA.requests.order;

import com.market.MSA.constants.PromoScopeType;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CampaignTargetRequest {
  Long campaignId;

  @NotNull(message = "TargetType không được để null")
  PromoScopeType targetType;

  Long targetId;
}
