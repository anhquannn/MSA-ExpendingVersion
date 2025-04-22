package com.market.MSA.responses.goship;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShipmentResponse {
  @JsonProperty("code")
  int code;

  @JsonProperty("status")
  String status;

  @JsonProperty("data")
  Object data; // giữ là Object vì JSON mẫu có thể có mảng hoặc null

  @JsonProperty("id")
  String id;

  @JsonProperty("shipment_status")
  int shipmentStatus;

  @JsonProperty("shipment_status_txt")
  String shipmentStatusTxt;

  @JsonProperty("cod")
  int cod;

  @JsonProperty("fee")
  int fee;

  @JsonProperty("tracking_number")
  String trackingNumber;

  @JsonProperty("carrier")
  String carrier;

  @JsonProperty("carrier_short_name")
  String carrierShortName;

  @JsonProperty("sorting_code")
  String sortingCode;

  @JsonProperty("created_at")
  String createdAt;
}
