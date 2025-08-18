package com.market.MSA.requests.filters;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;
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
  List<Long> categoryIds;
  Long supplierId;
  String unit;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  LocalDateTime fromDate;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  LocalDateTime toDate;

  String netWeight;

  @PositiveOrZero(message = "Minimum price must be a positive number or zero")
  Double minPrice;

  @PositiveOrZero(message = "Maximum price must be a positive number or zero")
  Double maxPrice;

  String keyword;

  // Sorting
  String sortBy;
  String sortDirection;

  // New filters
  String abcClassification; // 'A' | 'B' | 'C'
  Boolean isPromotional;
  Boolean isExemptFromPromotion;

  // Pagination
  @Min(value = 1, message = "Page number must be greater than 0")
  @Builder.Default
  int page = 1;

  @Min(value = 1, message = "Page size must be greater than 0")
  @Builder.Default
  int pageSize = 10;
}
