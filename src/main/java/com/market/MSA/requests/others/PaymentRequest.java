package com.market.MSA.requests.others;

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
public class PaymentRequest {
  String paymentMethod;
  String paymentDate;
  String status;
  double grandTotal;
  String transactionId;

  String bankCode;
  String bankTranNo;
  String responseCode;
  LocalDateTime updateDate;

  Long userId;
  Long orderId;
}
