package com.market.MSA.responses;

import java.util.Date;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StockTransferResponse {
  long stocktransferId;

  int quantity;
  String status;
  Date requestDate;
  Date confirmationDate;

  BranchResponse fromBranch;
  BranchResponse toBranch;
  ProductResponse product;
  UserResponse userRequest;
  UserResponse userResponse;
}
