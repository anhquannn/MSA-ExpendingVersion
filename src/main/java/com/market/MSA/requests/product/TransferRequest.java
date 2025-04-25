package com.market.MSA.requests.product;

import java.util.Date;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TransferRequest {
  String status;
  String note;
  Date createdAt;
  Date updatedAt;

  Long fromInventoryId;
  Long toInventoryId;
  Long requesterId;
  Long approverId;
}
