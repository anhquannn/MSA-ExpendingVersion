package com.market.MSA.requests;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ParcelRequest {
  String length;
  String width;
  String height;
  String distance_unit;
  String weight;
  String mass_unit;
}
