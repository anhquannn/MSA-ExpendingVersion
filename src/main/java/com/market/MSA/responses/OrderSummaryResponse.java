package com.market.MSA.responses;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderSummaryResponse {
  private double totalCost;
  private double discount;
  private double grandTotal;
}
