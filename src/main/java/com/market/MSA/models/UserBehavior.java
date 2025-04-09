package com.market.MSA.models;

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
@Table(name = "userBehaviors")
public class UserBehavior {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  long userBehaviorId;

  String behaviorType;
  Date timestamp;

  @ManyToOne
  @JoinColumn(name = "userId", nullable = false)
  User user;

  @ManyToOne
  @JoinColumn(name = "productId", nullable = false)
  Product product;
}
