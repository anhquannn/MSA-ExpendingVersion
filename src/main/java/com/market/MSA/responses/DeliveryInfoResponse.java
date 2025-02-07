package com.market.MSA.responses;

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
public class DeliveryInfoResponse {
  long deliveryInfoId;

  Date deliveryDate;
  String status;

  UserResponse user;
  OrderResponse order;
}
