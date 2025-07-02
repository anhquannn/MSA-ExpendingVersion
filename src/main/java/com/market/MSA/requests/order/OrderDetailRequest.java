package com.market.MSA.requests.order;

import com.market.MSA.constants.OrderStatus;
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
public class OrderDetailRequest {
  int quantity;
  double unitPrice;
  double totalPrice;
  String image;
  String name;
  OrderStatus status;
  boolean rated;

  Long orderId;
  Long productId;
}
