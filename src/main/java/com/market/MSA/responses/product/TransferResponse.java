package com.market.MSA.responses.product;

import com.market.MSA.responses.user.UserResponse;
import java.util.Date;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TransferResponse {
  Long transferRequestId;

  String status;
  String note;
  Date createdAt;
  Date updatedAt;

  UserResponse requesterResponse;
  UserResponse approverResponse;
  InventoryResponse fromInventoryResponse;
  InventoryResponse toInventoryResponse;
}
