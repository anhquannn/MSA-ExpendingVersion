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
public class ProductRequest {
  String name;
  String image;
  double price;
  int size;
  String color;
  String specification;
  String description;
  Date expiry;
  int stockNumber;
  String stockLevel;
  int sales;

  long manufactureId;
  long branchId;
  long categoryId;
}
