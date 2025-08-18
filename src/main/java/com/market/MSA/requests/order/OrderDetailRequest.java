package com.market.MSA.requests.order;

import com.market.MSA.constants.OrderStatus;
import jakarta.validation.constraints.NotNull;
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
  @Positive(message = "Số lượng phải > 0")
  int quantity;

  @PositiveOrZero(message = "Đơn giá phải >= 0")
  double unitPrice;

  @PositiveOrZero(message = "Tổng giá phải >= 0")
  double totalPrice;

  String image;
  String name;
  OrderStatus status;
  boolean rated;

  @NotNull(message = "orderId không được để null")
  Long orderId;

  @NotNull(message = "productId không được để null")
  Long productId;
}
