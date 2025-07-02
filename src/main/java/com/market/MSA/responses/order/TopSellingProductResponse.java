package com.market.MSA.responses.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopSellingProductResponse {
  private Long productId;
  private String name;
  private long totalQuantity;
}
