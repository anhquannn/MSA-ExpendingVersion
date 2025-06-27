package com.market.MSA.requests.product;

import com.market.MSA.constants.ProductStatus;
import java.time.LocalDateTime;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TransferRequest {
  ProductStatus status;
  String note;
  LocalDateTime createdAt;
  LocalDateTime updatedAt;

  Long fromInventoryId;
  Long toInventoryId;
  Long requesterId;
  Long approverId;
}
