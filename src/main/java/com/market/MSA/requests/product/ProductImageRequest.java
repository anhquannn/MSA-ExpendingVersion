package com.market.MSA.requests.product;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductImageRequest {
  String imageUrl;
  String isPrimary;
  String sortOrder;

  Long productId;
}
