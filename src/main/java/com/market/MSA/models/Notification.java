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
@Table(name = "notifications")
public class Notification {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  long notificationId;

  String notificationType;
  Date notificationDate;
  boolean isRead;
  String message;

  @ManyToOne
  @JoinColumn(name = "productId", nullable = false)
  Product product;

  @ManyToOne
  @JoinColumn(name = "orderId", nullable = false)
  Order order;

  @ManyToOne
  @JoinColumn(name = "inventoryId", nullable = false)
  Inventory inventory;

  @ManyToOne
  @JoinColumn(name = "userId", nullable = false)
  User user;
}
