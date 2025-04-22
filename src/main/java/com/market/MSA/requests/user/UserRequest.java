package com.market.MSA.requests.user;

import com.market.MSA.validators.DobConstraint;
import com.market.MSA.validators.PhoneNumberConstraint;
import java.time.LocalDate;
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
public class UserRequest {
  String fullName;

  String email;

  @PhoneNumberConstraint String phoneNumber;

  @DobConstraint(min = 18)
  LocalDate birthday;

  String password;
  String address;
  String googleId;

  List<Long> branches;
}
