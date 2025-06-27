package com.market.MSA.requests.order;

import com.market.MSA.constants.PromocodeStatus;
import java.time.LocalDateTime;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CampaignRequest {
  String name;
  String description;
  PromocodeStatus status;
  LocalDateTime startDate;
  LocalDateTime endDate;
}
