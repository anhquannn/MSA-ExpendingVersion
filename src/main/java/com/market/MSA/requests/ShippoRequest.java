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

  public static AddressRequest getDefaultAddressFrom() {
    return new AddressRequest(
        "MSA",
        "1092 Indian Summer Ct",
        "San Jose",
        "CA",
        "95122",
        "US",
        "4159876543",
        "nguyenanhquan20102003@gmail.com");
  }
}
