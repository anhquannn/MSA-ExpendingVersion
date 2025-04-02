package com.market.MSA.controllers;

import com.market.MSA.requests.*;
import com.market.MSA.responses.ApiResponse;
import com.market.MSA.responses.AuthenticationResponse;
import com.market.MSA.responses.UserResponse;
import com.market.MSA.services.AuthenticationService;
import com.market.MSA.services.UserService;
import com.nimbusds.jose.JOSEException;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import java.text.ParseException;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {
  UserService userService;
  AuthenticationService authenticationService;

  @PostMapping("/register")
  ApiResponse<UserResponse> registerUser(@RequestBody @Valid UserRequest request) {
    return ApiResponse.<UserResponse>builder().result(userService.registerUser(request)).build();
  }

  @PostMapping("/login")
  ApiResponse<String> login(@RequestBody @Valid AuthenticationRequest requests)
      throws MessagingException {
    return ApiResponse.<String>builder()
        .result(userService.login(requests.getEmail(), requests.getPassword()))
        .build();
  }

  @PostMapping("/verify-otp")
  ApiResponse<String> verifyOtp(@RequestBody @Valid VerifyOtpRequest requests) {
    return ApiResponse.<String>builder().result(userService.verifyOtp(requests.getOtp())).build();
  }

  @PostMapping("/reset-password")
  ApiResponse<String> resetPassword(@RequestBody @Valid UserRequest requests)
      throws MessagingException {
    return ApiResponse.<String>builder()
        .result(userService.resetPassword(requests.getEmail()))
        .build();
  }

  @PostMapping("/login/google")
  ApiResponse<String> loginWithGoogle(@RequestParam String accessToken) {
    return ApiResponse.<String>builder().result(userService.loginWithGoogle(accessToken)).build();
  }

  @PostMapping
  ApiResponse<UserResponse> createUser(@RequestBody @Valid UserRequest request) {
    return ApiResponse.<UserResponse>builder().result(userService.createUser(request)).build();
  }

  @GetMapping
  ApiResponse<List<UserResponse>> getUsers() {
    var authentication = SecurityContextHolder.getContext().getAuthentication();
    authentication
        .getAuthorities()
        .forEach(grantedAuthority -> log.info(grantedAuthority.getAuthority()));

    return ApiResponse.<List<UserResponse>>builder().result(userService.getUsers()).build();
  }

  @GetMapping("/{userId}")
  ApiResponse<UserResponse> getUser(@PathVariable long userId) {
    return ApiResponse.<UserResponse>builder().result(userService.getUserByID(userId)).build();
  }

  @GetMapping("/google/{googleId}")
  ApiResponse<UserResponse> getUserByGoogleID(@PathVariable String googleId) {
    return ApiResponse.<UserResponse>builder()
        .result(userService.getUserByGoogleID(googleId))
        .build();
  }

  @PostMapping("/reset-password/{email}")
  ApiResponse<String> generateAndSetRandomPasswordByEmail(@PathVariable String email) {
    return ApiResponse.<String>builder()
        .result(userService.generateAndSetRandomPasswordByEmail(email))
        .build();
  }

  @GetMapping("/email/{email}")
  ApiResponse<UserResponse> getUserByEmail(@PathVariable String email) {
    return ApiResponse.<UserResponse>builder().result(userService.getUserByEmail(email)).build();
  }

  @GetMapping("/myinfo")
  ApiResponse<UserResponse> getMyInfo() {
    return ApiResponse.<UserResponse>builder().result(userService.getMyInfo()).build();
  }

  @DeleteMapping("/{userId}")
  ApiResponse<Boolean> deleteUser(@PathVariable long userId) {
    Boolean result = userService.deleteUser(userId);
    return ApiResponse.<Boolean>builder().result(result).build();
  }

  @PutMapping("/{userId}")
  ApiResponse<UserResponse> updateUser(
      @PathVariable long userId, @RequestBody UpdateUserRequest request) {
    return ApiResponse.<UserResponse>builder()
        .result(userService.updateUser(userId, request))
        .build();
  }

  @PostMapping("/logout")
  ApiResponse<Void> logout(@RequestBody LogoutRequest request)
      throws JOSEException, ParseException {
    authenticationService.logout(request);
    return ApiResponse.<Void>builder().build();
  }

  @PostMapping("/refresh")
  ApiResponse<AuthenticationResponse> refresh(@RequestBody RefreshRequest request)
      throws JOSEException, ParseException {
    var result = authenticationService.refreshToken(request);
    return ApiResponse.<AuthenticationResponse>builder().result(result).build();
  }
}
