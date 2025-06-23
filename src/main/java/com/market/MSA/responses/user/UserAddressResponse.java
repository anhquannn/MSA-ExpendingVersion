package com.market.MSA.responses.user;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

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
