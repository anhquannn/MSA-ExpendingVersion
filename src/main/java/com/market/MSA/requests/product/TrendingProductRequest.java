package com.market.MSA.requests.product;

import java.util.Date;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TrendingProductRequest {
  double trendScore;
  Date timestamp;

  Long productId;
}
