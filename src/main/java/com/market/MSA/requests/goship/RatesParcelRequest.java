package com.market.MSA.requests.goship;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RatesParcelRequest {
  String cod;
  String length;
  String width;
  String height;
  String weight;
}
