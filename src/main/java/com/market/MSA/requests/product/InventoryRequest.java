package com.market.MSA.requests.product;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InventoryRequest {
  String name;
  String address;
  String contact;
  double totalRevenue;

  Long branchId;
}
