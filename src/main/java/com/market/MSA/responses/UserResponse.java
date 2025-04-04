package com.market.MSA.responses;

import com.market.MSA.models.User;
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
  long userId;
  String fullName;
  String email;
  String phoneNumber;
  String birthday;
  String password;
  String address;
  String googleId;

  Set<RoleResponse> roles;

  List<BranchResponse> branches;

  public static UserResponse fromUser(User user) {
    return UserResponse.builder()
        .userId(user.getUserId())
        .email(user.getEmail())
        .fullName(user.getFullName())
        .phoneNumber(user.getPhoneNumber())
        .birthday(user.getBirthday())
        .address(user.getAddress())
        .googleId(user.getGoogleId())
        .build();
  }
}
