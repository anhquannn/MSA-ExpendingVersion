package com.market.MSA.requests.product;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductFilterRequest {
  Long branchId;
  Long categoryId;
  Long supplierId;
  String unit;
  String netWeight;

  @PositiveOrZero(message = "Minimum price must be a positive number or zero")
  Double minPrice;

  @PositiveOrZero(message = "Maximum price must be a positive number or zero")
  Double maxPrice;

  String keyword;

  // Sorting
  String sortBy;
  String sortDirection;

  // Pagination
  @Min(value = 1, message = "Page number must be greater than 0")
  @Builder.Default
  int page = 1;

  @Min(value = 1, message = "Page size must be greater than 0")
  @Builder.Default
  int pageSize = 10;
}
