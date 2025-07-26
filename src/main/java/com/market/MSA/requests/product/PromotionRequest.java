package com.market.MSA.requests.product;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.market.MSA.constants.PromocodeStatus;
import com.market.MSA.validators.DateRangeConstraint;
import java.time.LocalDateTime;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@DateRangeConstraint(
    startDate = "startDate",
    endDate = "endDate",
    message =
        "Ngày bắt đầu và ngày kết thúc phải sau thời điểm hiện tại, và ngày kết thúc phải sau ngày bắt đầu")
public class PromotionRequest {
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  LocalDateTime startDate;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  LocalDateTime endDate;

  Integer discountPercentage;

  PromocodeStatus status;

  Long productMainId;
  Long productFreeId;
}
