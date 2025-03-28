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
        "123 Đường ABC",
        "Thành phố Hồ Chí Minh",
        "SG",
        "100000",
        "VN",
        "0937974995",
        "nguyenanhquan20102003@gmail.com");
  }
}
