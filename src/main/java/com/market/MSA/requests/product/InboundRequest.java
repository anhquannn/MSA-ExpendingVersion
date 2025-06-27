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
public class InboundRequest {
  ProductStatus status;
  LocalDateTime inboundTransferDate;

  Long userId;
  Long transferRequestId;
  Long inventoryId;
}
