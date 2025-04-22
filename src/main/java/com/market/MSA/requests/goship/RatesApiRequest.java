package com.market.MSA.requests.goship;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RatesApiRequest {
  RatesAddressRequest address_from;
  RatesAddressRequest address_to;
  RatesParcelRequest parcel;
}
