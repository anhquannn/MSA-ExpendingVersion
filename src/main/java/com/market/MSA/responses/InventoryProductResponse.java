package com.market.MSA.responses;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InventoryProductResponse {
  long inventoryProductId;
  int stockNumber;
  String stockLevel;

  ProductResponse product;
  InventoryResponse inventory;
}
