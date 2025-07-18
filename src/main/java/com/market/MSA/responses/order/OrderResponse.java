package com.market.MSA.responses.order;

import com.market.MSA.constants.OrderStatus;
import com.market.MSA.responses.others.DeliveryInfoResponse;
import com.market.MSA.responses.others.PaymentResponse;
import com.market.MSA.responses.product.BranchResponse;
import com.market.MSA.responses.user.UserResponse;
import java.time.LocalDateTime;
import java.util.List;
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
  Long orderId;

  LocalDateTime orderDate;
  double grandTotal;
  OrderStatus status;

  BranchResponse branch;
  CartResponse cart;
  UserResponse user;

  DeliveryInfoResponse deliveryInfo;
  List<PaymentResponse> payments;
  List<PromoCodeResponse> promoCodes;
  List<PromoCodeUsageResponse> promoCodeUsages;
}
