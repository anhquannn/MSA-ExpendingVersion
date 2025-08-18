package com.market.MSA.requests.filters;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InventoryProductFilterRequest {
  Long inventoryId;
  Long productId;

  boolean isActive;
  boolean isDiscounted;

  @Size(max = 50, message = "Batch number must be less than 50 characters")
  String batchNumber;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  LocalDateTime fromDate;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  LocalDateTime toDate;

  Double minStock;
  Double maxStock;
  Double minPrice;
  Double maxPrice;

  // Low stock filter
  Boolean isLowStock;

  // Sorting
  @Builder.Default String sortBy = "stockNumber";

  @Builder.Default String sortDirection = "DESC";

  // Pagination
  @Min(value = 1, message = "Page number must be greater than 0")
  @Builder.Default
  int page = 1;

  @Min(value = 1, message = "Page size must be greater than 0")
  @Builder.Default
  int pageSize = 10;
}
