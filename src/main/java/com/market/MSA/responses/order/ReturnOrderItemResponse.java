package com.market.MSA.responses.order;

import java.util.List;
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
public class ReturnOrderItemResponse {

  Long itemId;

  OrderDetailResponse orderDetail;

  int quantity;
  String conditionNote;
  String reason;

  List<ReturnItemImageResponse> images;
}
