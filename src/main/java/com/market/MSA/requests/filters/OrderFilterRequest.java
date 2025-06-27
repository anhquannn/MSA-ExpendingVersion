package com.market.MSA.requests.filters;

import com.market.MSA.constants.OrderStatus;
import jakarta.validation.constraints.Min;
import java.time.LocalDateTime;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderFilterRequest {
  // Filter fields
  Long branchId;
  Long userId;
  OrderStatus status;
  LocalDateTime fromDate;
  LocalDateTime toDate;
  String phoneNumber;

  // Sorting
  @Builder.Default String sortBy = "orderDate";

  @Builder.Default String sortDirection = "DESC";

  // Pagination
  @Min(value = 1, message = "Page number must be greater than 0")
  @Builder.Default
  int page = 1;

  @Min(value = 1, message = "Page size must be greater than 0")
  @Builder.Default
  int pageSize = 10;
}
