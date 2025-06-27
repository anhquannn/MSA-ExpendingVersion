package com.market.MSA.responses.product;

import com.market.MSA.constants.ProductStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InventoryCheckRequestResponse {
  Long icrId;
  ProductStatus status;
  String note;
  Long inventoryId;
  Long surveyorId;
  Long userId;
}
