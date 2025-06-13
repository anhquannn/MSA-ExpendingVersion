package com.market.MSA.responses.order;

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
  String status;
  LocalDateTime startDate;
  LocalDateTime endDate;

  List<PromoCodeResponse> promoCodes;
}
