package com.market.MSA.requests.order;

import com.market.MSA.constants.OrderStatus;
import java.time.LocalDateTime;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CancelOrderRequest {
  LocalDateTime cancelDate;
  OrderStatus status;
  String reason;

  @PositiveOrZero
  double refundAmount;

  Long orderId;
}
