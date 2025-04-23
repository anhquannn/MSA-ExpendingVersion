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
public class DistrictResponse {
  @JsonProperty("id")
  String id;

  @JsonProperty("name")
  String name;

  @JsonProperty("city_id")
  String city_id;

  @JsonProperty("support_carriers")
  List<String> support_carriers;
}
