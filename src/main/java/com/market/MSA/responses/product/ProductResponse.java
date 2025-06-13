package com.market.MSA.responses.product;

import com.market.MSA.responses.order.OrderDetailResponse;
import com.market.MSA.responses.others.NotificationResponse;
import com.market.MSA.responses.user.UserBehaviorResponse;
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
public class ProductResponse {
  Long productId;

  String name;
  double price;
  double discountPercentage;
  int discountTriggerDays;
  String unit;
  String netWeight;
  String specification;
  String description;
  LocalDateTime createdAt;
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
