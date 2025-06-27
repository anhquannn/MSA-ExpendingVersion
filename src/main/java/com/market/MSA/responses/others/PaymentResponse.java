package com.market.MSA.responses.others;

import com.market.MSA.constants.OrderStatus;
import com.market.MSA.responses.order.OrderResponse;
import com.market.MSA.responses.user.UserResponse;
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
public class PaymentResponse {
  Long paymentId;

  String paymentMethod;
  String paymentDate;
  OrderStatus status;
  double grandTotal;
  String transactionId;

  String bankCode;
  String bankTranNo;
  String responseCode;
  LocalDateTime updateDate;

  UserResponse user;
  OrderResponse orderResponse;
}
