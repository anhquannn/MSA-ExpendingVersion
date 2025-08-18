package com.market.MSA.requests.user;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserAddressRequest {

  String city;
  String district;
  String street;
  String ward;

  @NotBlank(message = "Mã thành phố không được để trống")
  String cityCode;

  @NotBlank(message = "Mã quận không được để trống")
  String districtCode;

  @NotBlank(message = "Mã phường không được để trống")
  String wardCode;

  boolean isPrimary;

  LocalDateTime createdAt;

  Long userId;
}
