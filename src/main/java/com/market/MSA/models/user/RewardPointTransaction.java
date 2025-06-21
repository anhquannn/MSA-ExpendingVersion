package com.market.MSA.models.user;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.market.MSA.models.order.Order;
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
@Table(name = "reward_point_transactions")
public class RewardPointTransaction {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long rewardPointTransactionId;

  double pointChange;
  String type;
  String description;
  LocalDateTime createdAt;

  @ManyToOne
  @JoinColumn(name = "userId", nullable = false)
  @JsonBackReference("user-reward-transactions")
  User user;

  @ManyToOne
  @JoinColumn(name = "orderId", nullable = false)
  @JsonBackReference("order-reward-transactions")
  Order order;
}
