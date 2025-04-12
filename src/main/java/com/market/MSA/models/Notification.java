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
@Table(
    name = "notifications",
    indexes = {
      @Index(name = "idx_notification_user", columnList = "user_id"),
      @Index(name = "idx_notification_order", columnList = "order_id"),
      @Index(name = "idx_notification_product", columnList = "product_id"),
      @Index(name = "idx_notification_inventory", columnList = "inventory_id"),
      @Index(name = "idx_notification_date", columnList = "createAt"),
      @Index(name = "idx_notification_read", columnList = "isRead")
    })
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
