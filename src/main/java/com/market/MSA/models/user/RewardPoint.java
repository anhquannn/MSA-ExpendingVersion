package com.market.MSA.models.user;

import jakarta.persistence.*;
import java.util.Date;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "reward_points")
public class RewardPoint {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long rewardPointId;

  double points;
  double totalEarned;
  double totalRedeemed;
  Date updatedAt;

  @ManyToOne
  @JoinColumn(name = "userId", nullable = false)
  User user;
}
