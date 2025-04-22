package com.market.MSA.requests.goship;

import java.util.List;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShipmentApiRequest {
  String rate;
  AddressRequest address_from;
  AddressRequest address_to;
  List<ParcelRequest> parcels;
}
