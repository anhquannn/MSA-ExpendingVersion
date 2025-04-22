package com.market.MSA.responses.product;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InventoryProductResponse {
  Long inventoryProductId;
  int stockNumber;
  String stockLevel;

  ProductResponse product;
  InventoryResponse inventory;
}
