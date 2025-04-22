package com.market.MSA.responses.goship;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WardApiResponse {
  @JsonProperty("code")
  int code;

  @JsonProperty("status")
  String status;

  @JsonProperty("data")
  List<WardResponse> data;
}
