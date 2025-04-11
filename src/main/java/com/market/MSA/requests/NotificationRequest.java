package com.market.MSA.requests;

import java.util.Date;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationRequest {
  String notificationType;
  Date notificationDate;
  boolean isRead;
  String message;

  long userId;
  long productId;
  long orderId;
  long inventoryId;
}
