package com.market.MSA.responses.others;

import com.market.MSA.responses.order.OrderResponse;
import com.market.MSA.responses.user.UserResponse;
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
public class PaymentResponse {
  Long paymentId;

  String paymentMethod;
  String paymentDate;
  String status;
  double grandTotal;
  String transactionId;

  String bankCode;
  String bankTranNo;
  String responseCode;
  Date updateDate;

  UserResponse user;
  OrderResponse orderResponse;
}
