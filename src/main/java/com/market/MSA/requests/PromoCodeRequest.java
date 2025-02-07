package com.market.MSA.requests;

import java.util.Date;
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
  Date startDate;
  Date endDate;
  String status;
  String discountType;
  double discountPercentage;
  double minimumOrderValue;
}
