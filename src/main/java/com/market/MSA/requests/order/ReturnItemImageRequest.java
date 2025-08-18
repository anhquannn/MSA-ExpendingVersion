package com.market.MSA.requests.order;

import jakarta.validation.constraints.NotBlank;
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
public class ReturnItemImageRequest {

  @NotBlank(message = "URL hình ảnh không được để trống")
  @Size(max = 500, message = "URL hình ảnh không được vượt quá 500 ký tự")
  String imageUrl;
}
