package com.market.MSA.responses.product;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductImageResponse {
  Long productImageId;

  String imageUrl;
  boolean isPrimary;
  int sortOrder;

  ProductResponse productResponse;
}
