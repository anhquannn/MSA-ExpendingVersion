package com.market.MSA.responses.order;

import com.market.MSA.constants.OrderStatus;
import com.market.MSA.responses.product.ProductResponse;
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
public class OrderDetailResponse {
  Long orderDetailId;

  int quantity;
  double unitPrice;
  double totalPrice;
  String image;
  String name;
  OrderStatus status;

  boolean rated;

  boolean isFreeItem;

  ProductResponse product;
  OrderResponse order;

  List<OrderDetailResponse> freeItems;
}
