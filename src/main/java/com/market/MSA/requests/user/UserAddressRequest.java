package com.market.MSA.requests.user;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

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
    String cityCode;
    String districtCode;
    String wardCode;
    boolean isPrimary;

    LocalDateTime createdAt;

    Long userId;
}
