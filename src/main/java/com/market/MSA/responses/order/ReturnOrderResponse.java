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
public class ReturnOrderResponse {
  Long returnOrderId;

  Date returnDate;
  String status;
  String reason;
  double refundAmount;

  Long orderId;
}
