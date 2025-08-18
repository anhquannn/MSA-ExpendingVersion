package com.market.MSA.requests.order;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.market.MSA.constants.PromocodeStatus;
import com.market.MSA.validators.DateRangeConstraint;
import com.market.MSA.validators.DiscountPercentageConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
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
public class PromoCodeRequest {
  String name;

  @NotBlank(message = "Mã khuyến mãi không được để trống")
  String code;

  String description;

  @NotNull(message = "Ngày bắt đầu không được để null")
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  LocalDateTime startDate;

  @NotNull(message = "Ngày kết thúc không được để null")
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  LocalDateTime endDate;

  PromocodeStatus status;

  @DiscountPercentageConstraint double discountPercentage;

  @NotNull(message = "campaignId không được để null")
  Long campaignId;
}
