package com.market.MSA.responses.order;

import com.market.MSA.responses.goship.RatesResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderSummaryResponse {
  double totalCost;
  double discount;
  double grandTotal;

  RatesResponse rates;
}
