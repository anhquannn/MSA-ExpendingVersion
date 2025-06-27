package com.market.MSA.requests.filters;

import com.market.MSA.constants.ProductStatus;
import jakarta.validation.constraints.Min;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InventoryCheckRequestFilterRequest {
  String keyword;
  Long inventoryId;
  Long surveyorId;
  ProductStatus status;

  @Builder.Default String sortBy = "icrId";

  @Builder.Default String sortDirection = "ASC";

  // paging
  @Min(1)
  @Builder.Default
  int page = 1;

  @Min(1)
  @Builder.Default
  int pageSize = 10;
}
