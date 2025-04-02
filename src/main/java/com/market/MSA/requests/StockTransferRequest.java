package com.market.MSA.requests;

import java.util.Date;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StockTransferRequest {
  int quantity;
  String status;
  Date requestDate;
  Date confirmationDate;

  long fromBranchId;
  long toBranchId;
  long productId;
  long userRequestId;
  long userResponseId;
}
