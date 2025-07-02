package com.market.MSA.dtos.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Simple DTO representing a product and the quantity the user wants to (re)order. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDto {
  private Long productId;
  private int quantity;
}
