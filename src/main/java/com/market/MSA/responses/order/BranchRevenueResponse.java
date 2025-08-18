package com.market.MSA.responses.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class BranchRevenueResponse {
  private Long branchId;
  private String name;
  private Double totalRevenue;
}
