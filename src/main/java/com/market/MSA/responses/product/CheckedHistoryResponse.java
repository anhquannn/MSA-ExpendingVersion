package com.market.MSA.responses.product;

import com.market.MSA.responses.user.UserResponse;
import java.time.LocalDateTime;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CheckedHistoryResponse {
  Long checkedHistoryId;

  LocalDateTime checkedDate;
  String note;

  UserResponse user;
  InventoryResponse inventory;
}
