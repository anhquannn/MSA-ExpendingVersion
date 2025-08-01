package com.market.MSA.requests.order;

import com.market.MSA.constants.ReturnStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
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
public class ReturnOrderRequest {

  @NotNull(message = "orderId không được để null")
  Long orderId;

  @NotNull(message = "userId không được để null")
  Long userId;

  BigDecimal refundAmount;

  ReturnStatus status;

  String shippingCode;
  String shippingStatus;

  @Size(max = 500, message = "Lý do không được vượt quá 500 ký tự")
  String reason;

  @NotEmpty(message = "Danh sách sản phẩm trả hàng không được để trống")
  @Valid
  List<ReturnOrderItemRequest> returnOrderItems;
}
