package com.market.MSA.requests.user;

import com.market.MSA.validators.PhoneNumberConstraint;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
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
public class UpdateUserRequest {
  String fullName;

  @NotBlank(message = "Email không được để trống")
  String email;

  @PhoneNumberConstraint String phoneNumber;

  LocalDateTime birthday;

  @NotBlank(message = "Mật khẩu không được để trống")
  String password;

  String image;
  String deviceId;
  String googleId;

  List<Long> roles;
}
