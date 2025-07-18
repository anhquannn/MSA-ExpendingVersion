package com.market.MSA.requests.product;

import jakarta.validation.constraints.NotBlank;
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
public class BranchRequest {
  @NotBlank(message = "Tên chi nhánh không được để trống")
  String name;

  String phone;
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

  InventoryRequest inventory;
}
