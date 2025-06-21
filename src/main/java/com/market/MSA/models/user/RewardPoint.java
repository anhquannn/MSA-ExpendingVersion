package com.market.MSA.models.user;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.time.LocalDateTime;
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
  LocalDateTime updatedAt;

  @ManyToOne
  @JoinColumn(name = "userId", nullable = false)
  @JsonBackReference("user-reward-points")
  User user;
}
