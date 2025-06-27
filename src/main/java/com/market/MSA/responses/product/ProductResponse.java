package com.market.MSA.responses.product;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import java.time.LocalDateTime;
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
public class ProductResponse implements Serializable {
  private static final long serialVersionUID = 1L;
  Long productId;

  String name;
  double price;
  // Giá hiện tại tại chi nhánh (nếu có truyền branchId)
  Double branchCurrentPrice;
  double discountPercentage;
  int discountTriggerDays;
  String unit;
  String netWeight;
  String specification;
  String description;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  LocalDateTime createdAt;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  LocalDateTime updatedAt;

  double totalRevenue;

  SupplierResponse supplier;
  CategoryResponse category;
  List<InventoryProductResponse> inventoryProductResponses;
  List<FeedbackResponse> feedbackResponses;
  List<ProductImageResponse> productImageResponses;
  List<TrendingProductResponse> trendingProductResponses;
}
