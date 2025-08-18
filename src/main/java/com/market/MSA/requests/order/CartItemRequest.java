package com.market.MSA.requests.order;

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
public class CartItemRequest {
  boolean isSelected;
  boolean isFreeItem;

  @PositiveOrZero(message = "Giá phải >= 0")
  double price;

  @Positive(message = "Số lượng phải > 0")
  int quantity;

  Long productId;
  Long cartId;
}
