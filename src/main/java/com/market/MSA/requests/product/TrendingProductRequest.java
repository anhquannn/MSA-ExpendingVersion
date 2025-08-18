package com.market.MSA.requests.product;

import jakarta.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TrendingProductRequest {
  @PositiveOrZero(message = "Điểm xu hướng phải >= 0")
  double trendScore;

  LocalDateTime timestamp;

  Long productId;
}
