package com.market.MSA.requests.goship;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddressRequest {
  String name;
  String phone;
  String street;
  String ward;
  String district;
  String city;
}
