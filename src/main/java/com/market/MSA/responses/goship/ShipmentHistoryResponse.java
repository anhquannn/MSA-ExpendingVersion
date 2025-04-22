package com.market.MSA.responses.goship;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShipmentHistoryResponse {
  @JsonProperty("status")
  int status;

  @JsonProperty("message")
  String message;

  @JsonProperty("status_text")
  String status_text;

  @JsonProperty("status_desc")
  String status_desc;

  @JsonProperty("updated_at")
  String updated_at;

  @JsonProperty("updated_time")
  long updated_time;
}
