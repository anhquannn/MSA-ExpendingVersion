package com.market.MSA.requests.user;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
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
public class RefreshRequest {
  @NotBlank(message = "Refresh token is required")
  @JsonProperty("refresh_token")
  @JsonAlias({"token", "refresh_token"}) // Accepts both 'token' and 'refresh_token' in JSON
  String refreshToken;
}
