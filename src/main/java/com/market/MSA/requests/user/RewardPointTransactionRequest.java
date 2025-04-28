package com.market.MSA.requests.user;

import java.util.Date;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RewardPointTransactionRequest {
  double pointChange;
  String type;
  String description;
  Date createdAt;

  Long userId;
  Long orderId;
}
