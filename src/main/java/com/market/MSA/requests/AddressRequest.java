package com.market.MSA.requests;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddressRequest {
  String name;
  String street1;
  String city;
  String state;
  String zip;
  String country;
  String phone;
  String email;
}
