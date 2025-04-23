package com.market.MSA.responses.order;

import java.util.Date;
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
public class CancelOrderResponse {
  Long cancelOrderId;

  Date cancelDate;
  String status;
  String reason;
  double refundAmount;

  OrderResponse orderResponse;
}
