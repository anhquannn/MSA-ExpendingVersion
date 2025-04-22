package com.market.MSA.requests.user;

import java.util.Date;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserBehaviorRequest {
  String behaviorType;
  Date timestamp;

  Long userId;
  Long productId;
}
