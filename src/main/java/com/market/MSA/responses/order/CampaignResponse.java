package com.market.MSA.responses.order;

import java.util.Date;
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
  Date startDate;
  Date endDate;
}
