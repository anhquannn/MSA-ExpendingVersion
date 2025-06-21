package com.market.MSA.responses.order;

import com.market.MSA.responses.user.UserResponse;
import java.time.LocalDateTime;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PromoCodeUsageResponse {
  Long promoCodeUsageId;

  LocalDateTime usedAt;

  OrderResponse orderResponse;
  PromoCodeResponse promoCodeResponse;
  UserResponse userResponse;
}
