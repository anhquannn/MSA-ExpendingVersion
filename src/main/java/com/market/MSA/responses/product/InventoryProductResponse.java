package com.market.MSA.responses.product;

import java.time.LocalDateTime;
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
  double currentPrice;
  LocalDateTime expDate;
  boolean isActive;
  boolean isDiscounted;
  String batchNumber;
  String stockLevel;

  ProductResponse product;
  InventoryResponse inventory;
}
