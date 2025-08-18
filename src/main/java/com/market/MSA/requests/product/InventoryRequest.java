package com.market.MSA.requests.product;

import jakarta.validation.constraints.PositiveOrZero;
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

  @PositiveOrZero double totalRevenue;

  Long branchId;
}
