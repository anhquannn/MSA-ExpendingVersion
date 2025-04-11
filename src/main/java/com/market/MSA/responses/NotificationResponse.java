package com.market.MSA.responses;

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
  long notificationId;
  String notificationType;
  Date notificationDate;
  boolean isRead;
  String message;

  UserResponse user;
  ProductResponse product;
  OrderResponse order;
  InventoryResponse inventory;
}
