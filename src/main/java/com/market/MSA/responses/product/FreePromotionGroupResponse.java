package com.market.MSA.responses.product;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

/** Nhóm khuyến mãi bundle: 1 sản phẩm chính kèm danh sách sản phẩm free. */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FreePromotionGroupResponse implements Serializable {
  private static final long serialVersionUID = 1L;

  ProductResponse mainProduct;

  @Builder.Default List<ProductResponse> freeProducts = new ArrayList<>();
}
