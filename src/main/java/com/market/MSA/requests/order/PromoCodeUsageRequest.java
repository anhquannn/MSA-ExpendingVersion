package com.market.MSA.requests.order;

import java.time.LocalDateTime;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PromoCodeUsageRequest {
  LocalDateTime usedAt;

  Long promoCodeId;
  Long orderId;
  Long userId;
}
