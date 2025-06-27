package com.market.MSA.responses.order;

import com.market.MSA.constants.PromocodeStatus;
import java.time.LocalDateTime;
import java.util.List;
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
  LocalDateTime startDate;
  LocalDateTime endDate;
  PromocodeStatus status;
  double discountPercentage;
  double minimumOrderValue;

  CampaignResponse campaignResponse;
  List<PromoCodeUsageResponse> promoCodeUsages;
}
