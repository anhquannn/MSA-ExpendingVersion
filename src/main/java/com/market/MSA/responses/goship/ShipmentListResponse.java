package com.market.MSA.responses.goship;

import java.util.List;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShipmentListResponse {
  int code;
  String status;
  List<ShipmentDetailResponse> data;
}
