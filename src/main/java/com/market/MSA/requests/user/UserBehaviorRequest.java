package com.market.MSA.requests.user;

import java.time.LocalDateTime;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserBehaviorRequest {
  String behaviorType;
  LocalDateTime timestamp;

  Long userId;
  Long productId;
}
