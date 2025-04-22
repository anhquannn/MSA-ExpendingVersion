package com.market.MSA.requests.product;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InventoryProductRequest {
  int stockNumber;
  String stockLevel;

  Long inventoryId;
  Long productId;
}
