package com.market.MSA.responses.goship;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShipmentDetailResponse {
  @JsonProperty("id")
  String id;

  @JsonProperty("order_id")
  String order_id;

  @JsonProperty("carrier_name")
  String carrier_name;

  @JsonProperty("carrier_logo")
  String carrier_logo;

  @JsonProperty("carrier_code")
  String carrier_code;

  @JsonProperty("service_name")
  String service_name;

  @JsonProperty("expected")
  String expected;

  @JsonProperty("expected_delivery_date")
  String expected_delivery_date;

  @JsonProperty("cod_fee")
  double cod_fee;

  @JsonProperty("insurrance_fee")
  double insurrance_fee;

  @JsonProperty("return_fee")
  double return_fee;

  @JsonProperty("re_delivery_fee")
  double re_delivery_fee;

  @JsonProperty("update_info_fee")
  double update_info_fee;

  @JsonProperty("overweight_fee")
  double overweight_fee;

  @JsonProperty("discount")
  double discount;

  @JsonProperty("delivery_fee")
  double delivery_fee;

  @JsonProperty("total_fee")
  double total_fee;

  @JsonProperty("service_fee")
  double service_fee;

  @JsonProperty("amount_return_shop")
  double amount_return_shop;

  @JsonProperty("status_code")
  int status_code;

  @JsonProperty("status_text")
  String status_text;

  @JsonProperty("status_desc")
  String status_desc;

  @JsonProperty("created_at")
  String created_at;

  @JsonProperty("updated_at")
  String updated_at;

  @JsonProperty("payer")
  int payer;

  @JsonProperty("payer_txt")
  String payer_txt;

  @JsonProperty("tracking_url")
  String tracking_url;

  @JsonProperty("note_update")
  String note_update;

  @JsonProperty("sorting_code")
  String sorting_code;

  @JsonProperty("is_return")
  int is_return;

  @JsonProperty("is_part_delivery")
  int is_part_delivery;

  @JsonProperty("is_lost")
  int is_lost;

  @JsonProperty("address_from")
  SDAddressResponse address_from;

  @JsonProperty("address_to")
  SDAddressResponse address_to;

  @JsonProperty("parcel")
  SDParcelResponse parcel;

  @JsonProperty("history")
  List<ShipmentHistoryResponse> history;
}
