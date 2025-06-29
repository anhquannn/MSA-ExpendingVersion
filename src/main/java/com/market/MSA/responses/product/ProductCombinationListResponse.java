package com.market.MSA.responses.product;

import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class ProductCombinationListResponse {
  List<ProductResponse> products;
}
