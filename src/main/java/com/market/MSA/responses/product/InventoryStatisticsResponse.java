package com.market.MSA.responses.product;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InventoryStatisticsResponse {
  int totalProducts; // Tổng số sản phẩm
  int totalQuantity; // Tổng số lượng
  int lowStockCount; // Số lượng hàng sắp hết
  int highStockCount; // Số lượng hàng dồi dào
}
