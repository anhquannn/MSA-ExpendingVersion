package com.market.MSA.responses;

import java.util.Date;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserBehaviorResponse {
  Long userBehaviorId;

  String behaviorType;
  Date timestamp;

  UserResponse user;
  ProductResponse product;
}
