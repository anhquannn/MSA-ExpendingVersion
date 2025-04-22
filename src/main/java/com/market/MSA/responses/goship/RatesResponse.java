package com.market.MSA.responses.goship;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RatesResponse {
  @JsonProperty("id")
  String id;

  @JsonProperty("carrier_name")
  String carrierName;

  @JsonProperty("carrier_logo")
  String carrierLogo;

  @JsonProperty("carrier_short_name")
  String carrierShortName;

  @JsonProperty("service")
  String service;

  @JsonProperty("expected")
  String expected;

  @JsonProperty("is_apply_only")
  boolean isApplyOnly;

  @JsonProperty("promotion_id")
  int promotionId;

  @JsonProperty("discount")
  int discount;

  @JsonProperty("weight_fee")
  int weightFee;

  @JsonProperty("location_first_fee")
  int locationFirstFee;

  @JsonProperty("location_step_fee")
  int locationStepFee;

  @JsonProperty("remote_area_fee")
  int remoteAreaFee;

  @JsonProperty("oil_fee")
  int oilFee;

  @JsonProperty("location_fee")
  int locationFee;

  @JsonProperty("cod_fee")
  int codFee;

  @JsonProperty("service_fee")
  int serviceFee;

  @JsonProperty("total_fee")
  int totalFee;

  @JsonProperty("total_amount")
  int totalAmount;

  @JsonProperty("total_amount_carrier")
  int totalAmountCarrier;

  @JsonProperty("total_amount_shop")
  int totalAmountShop;

  @JsonProperty("price_table_id")
  int priceTableId;

  @JsonProperty("insurrance_fee")
  int insurranceFee;

  @JsonProperty("return_fee")
  int returnFee;

  @JsonProperty("report")
  ReportResponse report;
}
