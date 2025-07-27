package com.market.MSA.responses.order;

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
public class CartItemResponse {
  Long cartItemId;

  boolean isSelected;
  boolean isFreeItem;
  double price;
  int quantity;

  ProductResponse product;
  CartResponse cart;

  // Các sản phẩm tặng kèm theo (chỉ populated đối với item chính)
  List<CartItemResponse> freeItems;
}
