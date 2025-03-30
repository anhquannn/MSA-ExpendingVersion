package com.market.MSA.responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ShippoAddress {
  @JsonProperty("name")
  String name;

  @JsonProperty("street1")
  String street1;

  @JsonProperty("city")
  String city;

  @JsonProperty("state")
  String state;

  @JsonProperty("zip")
  String zip;

  @JsonProperty("country")
  String country;

  @JsonProperty("phone")
  String phone;

  @JsonProperty("email")
  String email;

  @JsonProperty("object_id")
  String objectId;
}
