package com.market.MSA.responses.product;

import java.util.List;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InventoryResponse {
  Long inventoryId;

  String name;
  String address;
  String contact;
  double totalRevenue;

  BranchResponse branch;
  List<InventoryProductResponse> inventoryProductResponses;
}
