package com.market.MSA.models.others;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.market.MSA.models.order.Order;
import com.market.MSA.models.product.Inventory;
import com.market.MSA.models.product.Product;
import com.market.MSA.models.user.User;
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
@Table(
    name = "notifications",
    indexes = {
      @Index(name = "idx_notification_user", columnList = "user_id"),
      @Index(name = "idx_notification_order", columnList = "order_id"),
      @Index(name = "idx_notification_product", columnList = "product_id"),
      @Index(name = "idx_notification_inventory", columnList = "inventory_id"),
      @Index(name = "idx_notification_date", columnList = "notificationDate"),
      @Index(name = "idx_notification_read", columnList = "isRead")
    })
public class Notification {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long notificationId;

  String notificationType;
  String deviceTokens;
  String deviceIds;
  LocalDateTime notificationDate;
  boolean isRead;
  String message;

  @ManyToOne
  @JoinColumn(name = "productId")
  @JsonBackReference("product-notifications")
  Product product;

  @ManyToOne
  @JoinColumn(name = "orderId")
  @JsonBackReference("order-notifications")
  Order order;

  @ManyToOne
  @JoinColumn(name = "inventoryId")
  @JsonBackReference("inventory-notifications")
  Inventory inventory;

  @ManyToOne
  @JoinColumn(name = "userId")
  @JsonBackReference("user-notifications")
  User user;
}
