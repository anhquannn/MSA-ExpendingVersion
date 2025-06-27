package com.market.MSA.responses.user;

import com.market.MSA.models.user.User;
import com.market.MSA.responses.product.BranchResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
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
public class UserResponse {
  Long userId;
  String fullName;
  String email;
  String phoneNumber;
  LocalDateTime birthday;
  String password;
  String image;
  String deviceId;
  String googleId;

  Set<RoleResponse> roles;

  List<BranchResponse> branches;

  // List<UserAddressResponse> userAddresses;

  public static UserResponse fromUser(User user) {
    return UserResponse.builder()
        .userId(user.getUserId())
        .email(user.getEmail())
        .fullName(user.getFullName())
        .phoneNumber(user.getPhoneNumber())
        .birthday(user.getBirthday())
        .googleId(user.getGoogleId())
        .build();
  }
}
