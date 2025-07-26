package com.market.MSA.requests.product;

import com.market.MSA.constants.ABCClassification;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
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
  @NotBlank(message = "Tên sản phẩm không được để trống")
  String name;

  @PositiveOrZero(message = "Giá sản phẩm phải >= 0")
  double price;

  double discountPercentage;
  int discountTriggerDays;
  String unit;
  String netWeight;
  String specification;
  String description;
  LocalDateTime createdAt;
  LocalDateTime lastClassificationDate;
  boolean isPromotional;
  boolean isExemptFromPromotion;
  ABCClassification abcClassification;

  @PositiveOrZero double totalRevenue;

  Long supplierId;
  Long categoryId;
}
