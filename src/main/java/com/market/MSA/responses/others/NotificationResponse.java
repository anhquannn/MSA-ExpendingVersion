package com.market.MSA.responses.others;

import com.market.MSA.responses.order.OrderResponse;
import com.market.MSA.responses.product.InventoryResponse;
import com.market.MSA.responses.product.ProductResponse;
import com.market.MSA.responses.user.UserResponse;
import java.util.Date;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationResponse {
  Long notificationId;
  String notificationType;
  Date notificationDate;
  boolean isRead;
  String message;

  UserResponse user;
  ProductResponse product;
  OrderResponse order;
  InventoryResponse inventory;
}
