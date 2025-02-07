package com.market.MSA.controllers;

import com.market.MSA.requests.UpdateUserRequest;
import com.market.MSA.requests.UserRequest;
import com.market.MSA.responses.ApiResponse;
import com.market.MSA.responses.UserResponse;
import com.market.MSA.services.UserService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/users")
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {
  @Autowired private UserService userService;

  @PostMapping("/register")
  ApiResponse<String> registerUser(@RequestBody @Valid UserRequest request) {
    return ApiResponse.<String>builder().result(userService.registerUser(request)).build();
  }

  @PostMapping("/login")
  ApiResponse<String> login(@RequestParam String email, @RequestParam String password)
      throws MessagingException {
    return ApiResponse.<String>builder().result(userService.login(email, password)).build();
  }

  @PostMapping("/verify-otp")
  ApiResponse<String> verifyOtp(@RequestParam String otp) {
    return ApiResponse.<String>builder().result(userService.verifyOtp(otp)).build();
  }

  @PostMapping("/reset-password")
  ApiResponse<String> resetPassword(@RequestParam String email) throws MessagingException {
    return ApiResponse.<String>builder().result(userService.resetPassword(email)).build();
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
    log.info("Username: {}", authentication.getName());
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
  ApiResponse<String> deleteUser(@PathVariable long userId) {
    userService.deleteUser(userId);
    return ApiResponse.<String>builder().result("User has been deleted").build();
  }

  @PutMapping("/{userId}")
  ApiResponse<UserResponse> updateUser(
      @PathVariable long userId, @RequestBody UpdateUserRequest request) {
    return ApiResponse.<UserResponse>builder()
        .result(userService.updateUser(userId, request))
        .build();
  }
}
