package com.market.MSA.responses.others;

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
public class DeliveryInfoResponse {
  Long deliveryInfoId;

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
  String shipmentCode;

  Long orderId;
}
