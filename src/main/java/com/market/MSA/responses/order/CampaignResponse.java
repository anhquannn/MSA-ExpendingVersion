package com.market.MSA.responses.order;

import com.market.MSA.constants.PromocodeStatus;
import java.time.LocalDateTime;
import java.util.List;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CampaignResponse {
  Long campaignId;

  String name;
  String description;
  PromocodeStatus status;
  LocalDateTime startDate;
  LocalDateTime endDate;

  com.market.MSA.constants.PromoScopeType scopeType;
  double minOrderValue;

  List<PromoCodeResponse> promoCodes;
}
