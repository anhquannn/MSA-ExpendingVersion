package com.market.MSA.requests;

import java.util.List;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShippoRequest {
  AddressRequest address_from;
  AddressRequest address_to;
  List<ParcelRequest> parcels;
}
