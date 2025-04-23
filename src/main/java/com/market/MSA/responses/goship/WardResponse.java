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
public class WardResponse {
  @JsonProperty("id")
  int id;

  @JsonProperty("name")
  String name;

  @JsonProperty("district_id")
  String district_id;

  @JsonProperty("support_carriers")
  List<String> support_carriers;
}
