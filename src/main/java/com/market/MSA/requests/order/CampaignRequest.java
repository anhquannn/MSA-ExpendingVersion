package com.market.MSA.requests.order;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.market.MSA.constants.PromoScopeType;
import com.market.MSA.constants.PromocodeStatus;
import com.market.MSA.validators.DateRangeConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
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
public class CampaignRequest {
  @NotBlank(message = "Tên chiến dịch không được để trống")
  String name;

  String description;
  PromocodeStatus status;

  @NotNull(message = "Ngày bắt đầu không được để trống")
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  LocalDateTime startDate;

  @NotNull(message = "Ngày kết thúc không được để trống")
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  LocalDateTime endDate;

  PromoScopeType scopeType = PromoScopeType.ALL;

  @PositiveOrZero double minOrderValue;
}
