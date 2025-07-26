package com.market.MSA.requests.filters;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.market.MSA.validators.DateRangeConstraint;
import jakarta.validation.constraints.Min;
import java.time.LocalDateTime;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@DateRangeConstraint(
    startDate = "fromDate",
    endDate = "toDate",
    message =
        "Ngày bắt đầu và ngày kết thúc phải sau thời điểm hiện tại, và ngày kết thúc phải sau ngày bắt đầu")
public class TrendingProductFilterRequest {
  Long productId;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  LocalDateTime fromDate;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  LocalDateTime toDate;

  // Sorting
  @Builder.Default String sortBy = "score";

  @Builder.Default String sortDirection = "DESC";

  // Pagination
  @Min(value = 1, message = "Page number must be greater than 0")
  @Builder.Default
  int page = 1;

  @Min(value = 1, message = "Page size must be greater than 0")
  @Builder.Default
  int pageSize = 10;
}
