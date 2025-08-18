package com.market.MSA.responses.order;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpiringProductResponse {
  private Long productId;
  private String name;
  private long quantity;
  private LocalDateTime expDate;
}
