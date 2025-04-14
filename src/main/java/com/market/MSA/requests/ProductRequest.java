package com.market.MSA.requests;

import jakarta.validation.constraints.Positive;
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
public class ProductRequest {
  String name;
  String image;

  @Positive double price;

  @Positive double currentPrice;

  int size;
  String color;
  String specification;
  String description;
  Date expiry;
  Date createAt;

  @Positive double totalRevenue;

  Long manufactureId;
  Long categoryId;
}
