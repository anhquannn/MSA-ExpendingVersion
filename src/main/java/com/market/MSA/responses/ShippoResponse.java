package com.market.MSA.responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ShippoResponse {
  @JsonProperty("object_id")
   String objectId;

  @JsonProperty("object_created")
   String objectCreated;

  @JsonProperty("object_updated")
   String objectUpdated;

  @JsonProperty("object_owner")
   String objectOwner;

  @JsonProperty("test")
   boolean test;

  @JsonProperty("metadata")
   String metadata;

  @JsonProperty("messages")
   List<ShippoMessage> messages;

  @JsonProperty("extra")
   Map<String, Object> extra;

  @JsonProperty("order")
   String order;

  @JsonProperty("carrier_accounts")
   List<String> carrierAccounts;

  @JsonProperty("address_from")
   ShippoAddress addressFrom;

  @JsonProperty("address_to")
   ShippoAddress addressTo;

  @JsonProperty("parcels")
   List<ShippoParcel> parcels;

  @JsonProperty("status")
   String status;

  @JsonProperty("shipment_date")
   String shipmentDate;

  @JsonProperty("rates")
   List<ShippoRate> rates;

  @JsonProperty("address_return")
   ShippoAddress addressReturn;
}
