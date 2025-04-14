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
  Long deliveryInfoId;

  String street1;
  String city;
  String state;
  String zip;
  String country;
  String weight;
  String status;
  Date deliveryDate;

  Long orderId;
}
