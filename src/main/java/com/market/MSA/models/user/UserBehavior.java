package com.market.MSA.models.user;

import com.market.MSA.models.product.Product;
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
@Table(
    name = "user_behaviors",
    indexes = {
      @Index(name = "idx_behavior_user", columnList = "user_id"),
      @Index(name = "idx_behavior_product", columnList = "product_id"),
      @Index(name = "idx_behavior_type", columnList = "behaviorType"),
      @Index(name = "idx_behavior_timestamp", columnList = "timestamp")
    })
public class UserBehavior {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long userBehaviorId;

  String behaviorType;
  Date timestamp;

  @ManyToOne
  @JoinColumn(name = "userId", nullable = false)
  User user;

  @ManyToOne
  @JoinColumn(name = "productId", nullable = false)
  Product product;
}
