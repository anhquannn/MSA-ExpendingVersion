package com.market.MSA.requests.product;

import java.time.LocalDateTime;
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
  LocalDateTime createdAt;
  LocalDateTime updatedAt;

  Long fromInventoryId;
  Long toInventoryId;
  Long requesterId;
  Long approverId;
}
