package com.market.MSA.requests.others;

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

  Long userId;
  Long productId;
  Long orderId;
  Long inventoryId;
}
