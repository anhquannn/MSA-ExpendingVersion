package com.market.MSA.requests.order;

import com.market.MSA.constants.OrderStatus;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
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
  @Positive
  int quantity;

  @PositiveOrZero
  double unitPrice;

  @PositiveOrZero
  double totalPrice;

  String image;
  String name;
  OrderStatus status;
  boolean rated;

  Long orderId;
  Long productId;
}
