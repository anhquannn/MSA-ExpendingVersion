package com.market.MSA.responses.goship;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReportResponse {
  @JsonProperty("avg_time_delivery")
  int avgTimeDelivery;

  @JsonProperty("avg_time_delivery_format")
  int avgTimeDeliveryFormat;
}
