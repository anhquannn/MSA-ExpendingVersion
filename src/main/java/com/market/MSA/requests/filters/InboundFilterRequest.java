package com.market.MSA.requests.filters;

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
public class InboundFilterRequest {
  Long userId; // người tạo yêu cầu nhập kho
  Long inventoryId;
  Long transferId;
  ProductStatus status;
  LocalDateTime fromDate;
  LocalDateTime toDate;

  // Sắp xếp
  @Builder.Default String sortBy = "inboundTransferDate";

  @Builder.Default String sortDirection = "DESC";

  // Phân trang
  @Min(value = 1, message = "Page number must be greater than 0")
  @Builder.Default
  int page = 1;

  @Min(value = 1, message = "Page size must be greater than 0")
  @Builder.Default
  int pageSize = 10;
}
