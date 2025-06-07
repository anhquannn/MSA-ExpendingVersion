package com.market.MSA.responses.product;

import java.time.LocalDateTime;
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
  LocalDateTime timestamp;

  ProductResponse product;
}
