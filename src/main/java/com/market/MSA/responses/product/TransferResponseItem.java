package com.market.MSA.responses.product;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TransferResponseItem {
  Long transferRequestItemId;

  int quantityRequested;
  int quantityTransferred;

  ProductResponse productResponse;
  TransferResponse transferResponse;
}
