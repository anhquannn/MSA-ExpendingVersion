package com.market.MSA.responses;

import java.util.Date;
import java.util.List;
import java.util.Set;
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
public class OrderResponse {
  long orderId;

  Date orderDate;
  double grandTotal;
  double totalCost;
  String status;
  double discount;

  BranchResponse branch;
  CartResponse cart;
  UserResponse user;

  List<ReturnOrderResponse> returnOrders;
  List<PaymentResponse> payments;
  List<DeliveryInfoResponse> deliveryInfos;
  Set<PromoCodeResponse> promoCodes;
  List<OrderDetailResponse> orderDetails;
}
