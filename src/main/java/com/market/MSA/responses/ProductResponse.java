package com.market.MSA.responses;

import java.util.Date;
import java.util.List;
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
public class ProductResponse {
  long productId;

  String name;
  String image;
  double price;
  double currentPrice;
  int size;
  String color;
  String specification;
  String description;
  Date expiry;
  int totalRevenue;

  ManufacturerResponse manufacturer;
  CategoryResponse category;
  List<InventoryProductResponse> inventoryProductResponses;
  List<OrderDetailResponse> orderDetails;
}
