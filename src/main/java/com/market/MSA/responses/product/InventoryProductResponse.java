package com.market.MSA.responses.product;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InventoryProductResponse implements Serializable {
  private static final long serialVersionUID = 1L;
  Long inventoryProductId;

  int stockNumber;
  Integer stockNumberChecked;
  int stockNumberDifferent;
  double currentPrice;

  // giá hiện tại tại chi nhánh (để đồng bộ với ProductResponse)
  double branchCurrentPrice;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  LocalDateTime expDate;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  LocalDateTime createdAt;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  LocalDateTime updatedAt;

  boolean isActive;
  boolean isDiscounted;
  String batchNumber;
  String stockLevel;
  int minThreshold;
  int maxThreshold;

  ProductResponse product;
  InventoryResponse inventory;
}
