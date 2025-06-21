package com.market.MSA.responses.product;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.market.MSA.responses.order.OrderDetailResponse;
import com.market.MSA.responses.others.NotificationResponse;
import com.market.MSA.responses.user.UserBehaviorResponse;
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
  List<OrderDetailResponse> orderDetails;
  List<FeedbackResponse> feedbackResponses;
  List<ProductImageResponse> productImageResponses;
  List<UserBehaviorResponse> userBehaviorResponses;
  List<NotificationResponse> notificationResponses;
  List<TrendingProductResponse> trendingProductResponses;
}
