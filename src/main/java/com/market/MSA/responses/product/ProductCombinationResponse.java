package com.market.MSA.responses.product;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductCombinationResponse {
  Long combinationId;
  Long productId1;
  Long productId2;
}
