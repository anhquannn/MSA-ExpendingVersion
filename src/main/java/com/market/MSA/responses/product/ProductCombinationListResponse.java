package com.market.MSA.responses.product;

import com.market.MSA.responses.product.ProductResponse;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class ProductCombinationListResponse {
    List<ProductResponse> products;
}
