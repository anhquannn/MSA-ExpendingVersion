package com.market.MSA.models.user;

import com.market.MSA.models.order.Order;
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
@Table(name = "reward_point_transactions")
public class RewardPointTransaction {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long rewardPointTransactionId;

  double pointChange;
  String type;
  String description;
  Date createdAt;

  @ManyToOne
  @JoinColumn(name = "userId", nullable = false)
  User user;

  @ManyToOne
  @JoinColumn(name = "orderId", nullable = false)
  Order order;
}
