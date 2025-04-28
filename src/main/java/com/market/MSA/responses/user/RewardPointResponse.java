package com.market.MSA.responses.user;

import java.util.Date;
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
  Date updatedAt;

  UserResponse user;
}
