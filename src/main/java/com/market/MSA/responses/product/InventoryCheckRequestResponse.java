package com.market.MSA.responses.product;

import com.market.MSA.constants.ProductStatus;
import com.market.MSA.responses.user.UserResponse;
import java.time.LocalDateTime;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InventoryCheckRequestResponse {
  Long icrId;
  ProductStatus status;
  String note;
  LocalDateTime requestedDate;

  Long inventoryId;
  UserResponse surveyor;
  Long userId;
}
