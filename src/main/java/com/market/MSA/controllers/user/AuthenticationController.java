package com.market.MSA.controllers.user;

import com.market.MSA.requests.user.AuthenticationRequest;
import com.market.MSA.requests.user.IntrospectRequest;
import com.market.MSA.requests.user.LogoutRequest;
import com.market.MSA.requests.user.RefreshRequest;
import com.market.MSA.responses.others.ApiResponse;
import com.market.MSA.responses.user.AuthenticationResponse;
import com.market.MSA.responses.user.IntrospectResponse;
import com.market.MSA.services.user.AuthenticationService;
import com.nimbusds.jose.JOSEException;
import java.text.ParseException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {
  AuthenticationService authenService;

  @PostMapping("/login-non")
  ApiResponse<AuthenticationResponse> authen(@RequestBody AuthenticationRequest request) {
    var result = authenService.authenticate(request);
    return ApiResponse.<AuthenticationResponse>builder().result(result).build();
  }

  @PostMapping("/verify-non")
  ApiResponse<IntrospectResponse> verify(@RequestBody IntrospectRequest request)
      throws JOSEException, ParseException {
    var result = authenService.introspect(request);
    return ApiResponse.<IntrospectResponse>builder().result(result).build();
  }

  @PostMapping("/logout")
  ApiResponse<Void> logout(@RequestBody LogoutRequest request)
      throws JOSEException, ParseException {
    authenService.logout(request);
    return ApiResponse.<Void>builder().build();
  }

  @PostMapping("/refresh")
  ApiResponse<AuthenticationResponse> refresh(@RequestBody RefreshRequest request)
      throws JOSEException, ParseException {
    var result = authenService.refreshToken(request);
    return ApiResponse.<AuthenticationResponse>builder().result(result).build();
  }
}
