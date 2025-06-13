package com.market.MSA.requests.product;

import jakarta.validation.constraints.Positive;
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
public class ProductRequest {
  String name;

  @Positive double price;

  double discountPercentage;
  int discountTriggerDays;
  String unit;
  String netWeight;
  String specification;
  String description;
  LocalDateTime createdAt;

  @Positive double totalRevenue;

  Long supplierId;
  Long categoryId;
}
