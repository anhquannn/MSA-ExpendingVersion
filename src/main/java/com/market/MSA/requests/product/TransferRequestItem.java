package com.market.MSA.requests.product;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TransferRequestItem {
  int quantityRequested;
  int quantityTransferred;

  Long productId;
  Long transferRequestId;
}
