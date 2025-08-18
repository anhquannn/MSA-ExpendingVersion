package com.market.MSA.responses.order;

import com.market.MSA.constants.ReturnStatus;
import com.market.MSA.responses.user.UserResponse;
import java.math.BigDecimal;
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
public class ReturnOrderResponse {

  Long returnOrderId;

  OrderResponse order;
  UserResponse user;

  BigDecimal refundAmount;
  ReturnStatus status;

  String shippingCode;
  String shippingStatus;
  String reason;

  LocalDateTime createdAt;
  LocalDateTime updatedAt;

  List<ReturnOrderItemResponse> returnOrderItems;
}
