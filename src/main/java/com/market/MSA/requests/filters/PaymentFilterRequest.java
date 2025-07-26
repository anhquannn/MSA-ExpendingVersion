package com.market.MSA.requests.filters;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.market.MSA.constants.OrderStatus;
import com.market.MSA.validators.DateRangeConstraint;
import jakarta.validation.constraints.Min;
import java.time.LocalDateTime;
import java.util.List;
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
public class PaymentFilterRequest {
  List<Long> orderIds;
  Long orderId;
  Long userId;
  String paymentMethod;
  OrderStatus status;
  String transactionId;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  LocalDateTime fromDate;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  LocalDateTime toDate;

  Double minAmount;
  Double maxAmount;

  // Sorting
  @Builder.Default String sortBy = "paymentDate";

  @Builder.Default String sortDirection = "DESC";

  // Pagination
  @Min(value = 1, message = "Page number must be greater than 0")
  @Builder.Default
  int page = 1;

  @Min(value = 1, message = "Page size must be greater than 0")
  @Builder.Default
  int pageSize = 10;
}
