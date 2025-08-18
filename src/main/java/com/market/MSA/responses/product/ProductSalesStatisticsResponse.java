package com.market.MSA.responses.product;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductSalesStatisticsResponse {
  private List<MonthlySalesData> sales;
  private int stockNumber;
  private String stockLevel;
  private LocalDateTime expDate;
}
