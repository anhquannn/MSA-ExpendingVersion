package com.market.MSA.requests.order;

import com.market.MSA.constants.OrderStatus;
import com.market.MSA.requests.others.DeliveryInfoRequest;
import jakarta.validation.constraints.NotNull;
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

  @NotNull(message = "branchId không được để null")
  Long branchId;

  @NotNull(message = "cartId không được để null")
  Long cartId;

  @NotNull(message = "userId không được để null")
  Long userId;

  @NotNull(message = "userAddressId không được để null")
  Long userAddressId;

  List<String> promoCodes;
  DeliveryInfoRequest deliveryInfo;
}
