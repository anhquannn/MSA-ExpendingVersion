package com.market.MSA.requests.product;

import java.time.LocalDateTime;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InboundRequest {
  String status;
  LocalDateTime inboundTransferDate;

  Long userId;
  Long transferRequestId;
  Long inventoryId;
}
