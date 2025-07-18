package com.market.MSA.requests.order;

import com.market.MSA.constants.PromoScopeType;
import com.market.MSA.constants.PromocodeStatus;
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
public class CampaignRequest {
  @NotBlank(message = "Tên chiến dịch không được để trống")
  String name;

  String description;
  PromocodeStatus status;

  @NotNull(message = "Ngày bắt đầu không được để trống")
  LocalDateTime startDate;

  @NotNull(message = "Ngày kết thúc không được để trống")
  LocalDateTime endDate;

  PromoScopeType scopeType = PromoScopeType.ALL;

  @PositiveOrZero double minOrderValue;
}
