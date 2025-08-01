package com.market.MSA.responses.order;

import com.market.MSA.constants.ReturnStatus;
import java.math.BigDecimal;
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
public class ReturnOrderSummaryResponse {

  Long returnOrderId;
  Long orderId;
  String orderCode;

  String customerName;
  String customerPhone;

  ReturnStatus status;
  BigDecimal refundAmount;

  int totalItems;
  String reason;

  LocalDateTime createdAt;
  LocalDateTime updatedAt;
}
