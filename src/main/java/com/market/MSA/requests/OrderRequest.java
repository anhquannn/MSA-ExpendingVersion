package com.market.MSA.requests;

import jakarta.validation.constraints.Positive;
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
public class OrderRequest {
  Date orderDate;

  @Positive double grandTotal;

  String status;

  Long branchId;
  Long cartId;
  Long userId;
  List<String> promoCodes;
  DeliveryInfoRequest deliveryInfo;
}
