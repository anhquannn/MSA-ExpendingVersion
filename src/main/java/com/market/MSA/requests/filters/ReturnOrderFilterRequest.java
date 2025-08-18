package com.market.MSA.requests.filters;

import com.market.MSA.constants.ReturnStatus;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

/**
 * Request DTO for filtering return orders Supports filtering by status, user, order, branch, and
 * date range
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReturnOrderFilterRequest {

  /** Filter by return order status */
  ReturnStatus status;

  /** Filter by user ID */
  Long userId;

  /** Filter by order ID */
  Long orderId;

  /** Filter by branch ID */
  Long branchId;

  /** Filter by creation date from (inclusive) */
  LocalDateTime fromDate;

  /** Filter by creation date to (inclusive) */
  LocalDateTime toDate;

  /** Page number for pagination (0-based) */
  @Builder.Default Integer page = 0;

  /** Page size for pagination */
  @Builder.Default Integer size = 10;

  /** Sort field for ordering results */
  @Builder.Default String sortBy = "createdAt";

  /** Sort direction (ASC or DESC) */
  @Builder.Default String sortDirection = "DESC";
}
