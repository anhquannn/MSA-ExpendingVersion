package com.market.MSA.requests.others;

import com.market.MSA.constants.OrderStatus;
import java.time.LocalDateTime;
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
public class DeliveryInfoRequest {
  String street;
  String ward;
  String district;
  String city;
  String cityCode;
  String districtCode;
  String wardCode;
  String cod;
  String weight;
  String width;
  String height;
  String length;
  String metadata;
  OrderStatus status;
  LocalDateTime deliveryDate;

  Long orderId;
}
