package com.market.MSA.responses.product;

import java.util.Date;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TrendingProductResponse {
  Long trendId;
  double trendScore;
  Date timestamp;

  ProductResponse product;
}
