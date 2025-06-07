package com.market.MSA.requests.product;

import java.time.LocalDateTime;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TrendingProductRequest {
  double trendScore;
  LocalDateTime timestamp;

  Long productId;
}
