package com.market.MSA.requests.order;

import com.market.MSA.constants.ReturnStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class ReturnOrderStatusUpdateRequest {

  @NotNull(message = "Status không được để null")
  ReturnStatus status;

  @Size(max = 500, message = "Lý do không được vượt quá 500 ký tự")
  String reason;

  String shippingCode;
  String shippingStatus;
}
