package com.market.MSA.responses.user;

import com.market.MSA.responses.order.OrderResponse;
import java.time.LocalDateTime;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RewardPointTransactionResponse {
  Long rewardPointTransactionId;

  double pointChange;
  String type;
  String description;
  LocalDateTime createdAt;

  UserResponse user;
  OrderResponse order;
}
