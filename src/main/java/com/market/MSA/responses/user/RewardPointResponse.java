package com.market.MSA.responses.user;

import java.time.LocalDateTime;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RewardPointResponse {
  Long rewardPointId;

  double points;
  double totalEarned;
  double totalRedeemed;
  LocalDateTime updatedAt;

  UserResponse user;
}
