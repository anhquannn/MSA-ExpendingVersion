package com.market.MSA.responses.product;

import com.market.MSA.responses.order.OrderDetailResponse;
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
  Long productId;

  String name;
  String images;
  double price;
  double currentPrice;
  String unit;
  String color;
  String specification;
  String description;
  Date expiry;
  Date createAt;
  int totalRevenue;

  ManufacturerResponse manufacturer;
  CategoryResponse category;
  List<InventoryProductResponse> inventoryProductResponses;
  List<OrderDetailResponse> orderDetails;
}
