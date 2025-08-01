package com.market.MSA.requests.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
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
public class ReturnOrderItemRequest {

  @NotNull(message = "orderDetailId không được để null")
  Long orderDetailId;

  @Positive(message = "Số lượng phải > 0")
  int quantity;

  @Size(max = 500, message = "Ghi chú tình trạng không được vượt quá 500 ký tự")
  String conditionNote;

  @Size(max = 500, message = "Lý do không được vượt quá 500 ký tự")
  String reason;

  @Valid List<ReturnItemImageRequest> images;
}
