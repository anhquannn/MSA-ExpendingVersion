package com.market.MSA.requests.order;

import com.market.MSA.constants.PromocodeStatus;
import com.market.MSA.validators.DiscountPercentageConstraint;
import com.market.MSA.validators.PositiveAmountConstraint;
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
public class PromoCodeRequest {
  String name;
  String code;
  String description;
  LocalDateTime startDate;
  LocalDateTime endDate;
  PromocodeStatus status;

  @DiscountPercentageConstraint double discountPercentage;

  @PositiveAmountConstraint double minimumOrderValue;

  Long campaignId;
}
