package com.market.MSA.requests.user;

import com.market.MSA.constants.RewardPointTransactionType;
import java.time.LocalDateTime;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RewardPointTransactionRequest {
  double pointChange;
  RewardPointTransactionType type;
  String description;
  LocalDateTime createdAt;

  Long userId;
  Long orderId;
}
