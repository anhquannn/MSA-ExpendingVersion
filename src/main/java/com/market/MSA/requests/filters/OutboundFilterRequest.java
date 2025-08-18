package com.market.MSA.requests.filters;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.market.MSA.constants.ProductStatus;
import jakarta.validation.constraints.Min;
import java.time.LocalDateTime;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OutboundFilterRequest {
  Long userId;
  Long inventoryId;
  Long transferId;
  ProductStatus status;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  LocalDateTime fromDate;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  LocalDateTime toDate;

  // Sắp xếp
  @Builder.Default String sortBy = "outboundTransferDate";

  @Builder.Default String sortDirection = "DESC";

  // Phân trang
  @Min(value = 1, message = "Page number must be greater than 0")
  @Builder.Default
  int page = 1;

  @Min(value = 1, message = "Page size must be greater than 0")
  @Builder.Default
  int pageSize = 10;
}
