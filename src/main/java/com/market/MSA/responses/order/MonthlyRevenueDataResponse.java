package com.market.MSA.responses.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Monthly revenue data DTO */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyRevenueDataResponse {
  private int year;
  private int month;
  private double revenue;
}
