package com.market.MSA.responses.product;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BranchResponse {
  Long branchId;

  String name;
  String phone;
  String street;
  String ward;
  String district;
  String city;

  InventoryResponse inventory;
}
