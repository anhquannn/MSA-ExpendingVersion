package com.market.MSA.responses;

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
  String address;
  String city;
  String state;
  String zip;
  String country;
  String email;
  String contact;

  InventoryResponse inventory;
}
