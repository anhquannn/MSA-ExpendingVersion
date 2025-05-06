package com.market.MSA.models.others;

import com.market.MSA.models.order.Order;
import com.market.MSA.models.product.Inventory;
import com.market.MSA.models.product.Product;
import com.market.MSA.models.user.User;
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
      @Index(name = "idx_notification_date", columnList = "notificationDate"),
      @Index(name = "idx_notification_read", columnList = "isRead")
    })
public class Notification {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long notificationId;

  String notificationType;
  Date notificationDate;
  boolean isRead;
  String message;

  @ManyToOne
  @JoinColumn(name = "productId")
  Product product;

  @ManyToOne
  @JoinColumn(name = "orderId")
  Order order;

  @ManyToOne
  @JoinColumn(name = "inventoryId")
  Inventory inventory;

  @ManyToOne
  @JoinColumn(name = "userId")
  User user;
}
