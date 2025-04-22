package com.market.MSA.responses.goship;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SDAddressResponse {
  @JsonProperty("name")
  String name;

  @JsonProperty("phone")
  String phone;

  @JsonProperty("email")
  String email;

  @JsonProperty("street")
  String street;

  @JsonProperty("ward")
  String ward;

  @JsonProperty("ward_code")
  String ward_code;

  @JsonProperty("district")
  String district;

  @JsonProperty("district_code")
  int district_code;

  @JsonProperty("city")
  String city;

  @JsonProperty("city_code")
  int city_code;
}
