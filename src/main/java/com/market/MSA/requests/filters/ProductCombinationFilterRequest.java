package com.market.MSA.requests.filters;

import jakarta.validation.constraints.Min;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductCombinationFilterRequest {
  Long productId1;
  Long productId2;

  @Builder.Default String sortBy = "combinationId";

  @Builder.Default String sortDirection = "ASC";

  @Min(1)
  @Builder.Default
  int page = 1;

  @Min(1)
  @Builder.Default
  int pageSize = 10;
}
