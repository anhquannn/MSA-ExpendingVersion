package com.market.MSA.responses.user;

import java.time.LocalDateTime;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserAddressResponse {
  Long userAddressId;

  String city;
  String district;
  String street;
  String ward;
  String cityCode;
  String districtCode;
  String wardCode;
  boolean isPrimary;

  LocalDateTime createdAt;

  UserResponse user;
}
