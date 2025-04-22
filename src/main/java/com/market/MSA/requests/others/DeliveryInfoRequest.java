package com.market.MSA.requests.others;

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
public class DeliveryInfoRequest {
  String street;
  String ward;
  String district;
  String city;
  String cod;
  String weight;
  String width;
  String height;
  String length;
  String metadata;
  String status;
  Date deliveryDate;

  Long orderId;
}
