package com.market.MSA.responses.order;

import com.market.MSA.responses.product.ProductResponse;
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
public class OrderDetailResponse {
  Long orderDetailId;

  int quantity;
  double unitPrice;
  double totalPrice;
  String image;

  ProductResponse product;
  OrderResponse order;
}
