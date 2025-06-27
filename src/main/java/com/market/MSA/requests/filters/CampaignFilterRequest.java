package com.market.MSA.requests.filters;

import com.market.MSA.constants.PromocodeStatus;
import jakarta.validation.constraints.Min;
import java.time.LocalDateTime;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CampaignFilterRequest {
  String name;
  PromocodeStatus status;
  LocalDateTime fromDate;
  LocalDateTime toDate;
  String keyword;

  // Sorting
  @Builder.Default String sortBy = "startDate";

  @Builder.Default String sortDirection = "DESC";

  // Pagination
  @Min(value = 1, message = "Page number must be greater than 0")
  @Builder.Default
  int page = 1;

  @Min(value = 1, message = "Page size must be greater than 0")
  @Builder.Default
  int pageSize = 10;
}
