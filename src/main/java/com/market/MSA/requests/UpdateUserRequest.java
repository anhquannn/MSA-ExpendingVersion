package com.market.MSA.requests;

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
  String fullname;

  String email;
  String phonenumber;
  String birthday;
  String password;
  String address;
  String googleid;

  List<Long> roles;
}
