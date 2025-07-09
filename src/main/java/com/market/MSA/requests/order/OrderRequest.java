package com.market.MSA.requests.order;

import com.market.MSA.constants.OrderStatus;
import com.market.MSA.requests.others.DeliveryInfoRequest;
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
public class OrderRequest {
  LocalDateTime orderDate;

  double grandTotal;

  OrderStatus status;
  double usePoints;

  Long branchId;
  Long cartId;
  Long userId;
  Long userAddressId;
  List<String> promoCodes;
  DeliveryInfoRequest deliveryInfo;
}
