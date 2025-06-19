package com.market.MSA.requests.filters;

import jakarta.validation.constraints.Min;
import java.time.LocalDateTime;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RewardPointTransactionFilterRequest {
  Long userId;
  Long orderId;
  String transactionType;
  LocalDateTime fromDate;
  LocalDateTime toDate;
  Double minPoints;
  Double maxPoints;

  // Sorting
  @Builder.Default String sortBy = "createdAt";

  @Builder.Default String sortDirection = "DESC";

  // Pagination
  @Min(value = 1, message = "Page number must be greater than 0")
  @Builder.Default
  int page = 1;

  @Min(value = 1, message = "Page size must be greater than 0")
  @Builder.Default
  int pageSize = 10;
}
