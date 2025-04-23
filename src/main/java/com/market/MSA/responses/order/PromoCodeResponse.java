package com.market.MSA.responses.order;

import java.util.Date;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PromoCodeResponse {
  Long promoCodeId;

  String name;
  String code;
  String description;
  Date startDate;
  Date endDate;
  String status;
  String discountType;
  double discountPercentage;
  double minimumOrderValue;

  CampaignResponse campaignResponse;
}
