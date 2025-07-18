package com.market.MSA.requests.others;

import com.market.MSA.constants.OrderStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
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
public class DeliveryInfoRequest {
  String street;
  String ward;
  String district;
  String city;

  @NotBlank(message = "Mã thành phố không được để trống")
  String cityCode;

  @NotBlank(message = "Mã quận không được để trống")
  String districtCode;

  @NotBlank(message = "Mã phường không được để trống")
  String wardCode;

  String cod;
  String weight;
  String width;
  String height;
  String length;
  String metadata;
  OrderStatus status;

  @NotNull(message = "Ngày giao hàng không được để trống")
  LocalDateTime deliveryDate;

  Long orderId;
}
