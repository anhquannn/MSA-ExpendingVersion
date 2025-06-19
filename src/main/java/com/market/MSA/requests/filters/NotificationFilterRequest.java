package com.market.MSA.requests.filters;

import jakarta.validation.constraints.Min;
import java.time.LocalDateTime;
import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * Filter request for notifications with support for: - Filtering by user, type, read status,
 * related entities (product, order, inventory) - Date range filtering - Sorting - Pagination
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationFilterRequest {
  // Filter fields
  Long userId;
  String type;
  Boolean isRead;
  Long productId;
  Long orderId;
  Long inventoryId;
  LocalDateTime fromDate;
  LocalDateTime toDate;

  // Sorting
  @Builder.Default String sortBy = "notificationDate"; // Default sort by notification date

  @Builder.Default String sortDirection = "DESC"; // Default: newest first

  // Pagination
  @Min(value = 1, message = "Page number must be greater than 0")
  @Builder.Default
  int page = 1; // 1-based page number

  @Min(value = 1, message = "Page size must be greater than 0")
  @Builder.Default
  int pageSize = 10; // Default page size

  /**
   * Get the related ID based on the notification type This is a convenience method to get the
   * appropriate ID based on the type
   */
  public Long getRelatedId() {
    if (orderId != null) return orderId;
    if (productId != null) return productId;
    if (inventoryId != null) return inventoryId;
    return null;
  }
}
