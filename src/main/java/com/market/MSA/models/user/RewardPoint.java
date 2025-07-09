package com.market.MSA.models.user;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.time.LocalDateTime;

import jakarta.validation.constraints.PositiveOrZero;
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

  @PositiveOrZero
  double points;

  @PositiveOrZero
  double totalEarned;

  @PositiveOrZero
  double totalRedeemed;

  LocalDateTime updatedAt;

  @ManyToOne
  @JoinColumn(
      name = "user_id",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_reward_point_user"))
  @JsonBackReference("user-reward-points")
  User user;
}
