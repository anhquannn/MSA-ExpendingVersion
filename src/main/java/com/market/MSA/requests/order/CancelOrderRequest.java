package com.market.MSA.requests.order;

import com.market.MSA.constants.OrderStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
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
  @NotNull(message = "Ngày huỷ không được để trống")
  LocalDateTime cancelDate;

  OrderStatus status;
  String reason;

  @PositiveOrZero(message = "Số tiền hoàn trả phải >= 0")
  double refundAmount;

  @NotNull(message = "orderId không được để null")
  Long orderId;
}
