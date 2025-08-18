package com.market.MSA.responses.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** DTO for representing a customer with total spending in a given period. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopCustomerResponse {
  private Long customerId;
  private String name;
  private Double totalSpent;
}
