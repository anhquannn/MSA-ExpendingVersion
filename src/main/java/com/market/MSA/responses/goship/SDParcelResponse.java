package com.market.MSA.responses.goship;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SDParcelResponse {
  @JsonProperty("name")
  String name;

  @JsonProperty("quantity")
  int quantity;

  @JsonProperty("width")
  double width;

  @JsonProperty("height")
  double height;

  @JsonProperty("length")
  double length;

  @JsonProperty("weight")
  double weight;

  @JsonProperty("cweight")
  double cweight;

  @JsonProperty("cod_amount")
  double cod_amount;

  @JsonProperty("metadata")
  String metadata;
}
