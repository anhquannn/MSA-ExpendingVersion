package com.market.MSA.responses.user;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class AuthenticationResponse {
  @JsonProperty("access_token")
  String accessToken;

  @JsonProperty("refresh_token")
  String refreshToken;

  @JsonProperty("token_type")
  final String tokenType = "Bearer";

  @JsonProperty("expires_in")
  Long expiresIn;

  boolean authenticated;
}
