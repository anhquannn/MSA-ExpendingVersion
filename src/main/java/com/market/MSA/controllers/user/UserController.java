package com.market.MSA.controllers.user;

import com.market.MSA.constants.ApiMessage;
import com.market.MSA.requests.user.*;
import com.market.MSA.responses.others.ApiResponse;
import com.market.MSA.responses.user.AuthenticationResponse;
import com.market.MSA.responses.user.UserResponse;
import com.market.MSA.services.user.AuthenticationService;
import com.market.MSA.services.user.UserService;
import com.nimbusds.jose.JOSEException;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import java.text.ParseException;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
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
    return ApiResponse.<UserResponse>builder()
        .result(userService.registerUser(request))
        .message(ApiMessage.USER_REGISTERED.getMessage())
        .build();
  }

  @PostMapping("/login")
  ApiResponse<String> login(@RequestBody @Valid AuthenticationRequest requests)
      throws MessagingException {
    return ApiResponse.<String>builder()
        .result(userService.login(requests.getEmail(), requests.getPassword()))
        .message(ApiMessage.USER_LOGGED_IN.getMessage())
        .build();
  }

  @PostMapping("/verify-otp")
  ApiResponse<String> verifyOtp(@RequestBody @Valid VerifyOtpRequest requests) {
    return ApiResponse.<String>builder()
        .result(userService.verifyOtp(requests.getOtp()))
        .message(ApiMessage.EMAIL_VERIFIED.getMessage())
        .build();
  }

  @PostMapping("/reset-password")
  ApiResponse<String> resetPassword(@RequestBody @Valid UserRequest requests)
      throws MessagingException {
    return ApiResponse.<String>builder()
        .result(userService.resetPassword(requests.getEmail()))
        .message(ApiMessage.PASSWORD_RESET.getMessage())
        .build();
  }

  @PostMapping("/login/google")
  ApiResponse<String> loginWithGoogle(@RequestParam String accessToken) {
    return ApiResponse.<String>builder()
        .result(userService.loginWithGoogle(accessToken))
        .message(ApiMessage.GOOGLE_LOGIN_SUCCESSFUL.getMessage())
        .build();
  }

  @PostMapping
  ApiResponse<UserResponse> createUser(@RequestBody @Valid UserRequest request) {
    return ApiResponse.<UserResponse>builder()
        .result(userService.createUser(request))
        .message(ApiMessage.USER_CREATED.getMessage())
        .build();
  }

  @GetMapping
  ApiResponse<List<UserResponse>> getUsers() {
    var authentication = SecurityContextHolder.getContext().getAuthentication();
    authentication
        .getAuthorities()
        .forEach(grantedAuthority -> log.info(grantedAuthority.getAuthority()));

    return ApiResponse.<List<UserResponse>>builder()
        .result(userService.getUsers())
        .message(ApiMessage.ALL_USERS_RETRIEVED.getMessage())
        .build();
  }

  @GetMapping("/{userId}")
  ApiResponse<UserResponse> getUser(@PathVariable long userId) {
    return ApiResponse.<UserResponse>builder()
        .result(userService.getUserByID(userId))
        .message(ApiMessage.USER_RETRIEVED.getMessage())
        .build();
  }

  @GetMapping("/google/{googleId}")
  ApiResponse<UserResponse> getUserByGoogleID(@PathVariable String googleId) {
    return ApiResponse.<UserResponse>builder()
        .result(userService.getUserByGoogleID(googleId))
        .message(ApiMessage.USER_RETRIEVED.getMessage())
        .build();
  }

  @PostMapping("/reset-password/{email}")
  ApiResponse<String> generateAndSetRandomPasswordByEmail(@PathVariable String email) {
    return ApiResponse.<String>builder()
        .result(userService.generateAndSetRandomPasswordByEmail(email))
        .message(ApiMessage.PASSWORD_RESET_EMAIL_SENT.getMessage())
        .build();
  }

  @GetMapping("/email/{email}")
  ApiResponse<UserResponse> getUserByEmail(@PathVariable String email) {
    return ApiResponse.<UserResponse>builder()
        .result(userService.getUserByEmail(email))
        .message(ApiMessage.USER_RETRIEVED.getMessage())
        .build();
  }

  @GetMapping("/myinfo")
  ApiResponse<UserResponse> getMyInfo() {
    return ApiResponse.<UserResponse>builder()
        .result(userService.getMyInfo())
        .message(ApiMessage.USER_RETRIEVED.getMessage())
        .build();
  }

  @DeleteMapping("/{userId}")
  ApiResponse<Boolean> deleteUser(@PathVariable long userId) {
    Boolean result = userService.deleteUser(userId);
    return ApiResponse.<Boolean>builder()
        .result(result)
        .message(ApiMessage.USER_DELETED.getMessage())
        .build();
  }

  @PutMapping("/{userId}")
  ApiResponse<UserResponse> updateUser(
      @PathVariable long userId, @RequestBody UpdateUserRequest request) {
    return ApiResponse.<UserResponse>builder()
        .result(userService.updateUser(userId, request))
        .message(ApiMessage.USER_UPDATED.getMessage())
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
    return ApiResponse.<AuthenticationResponse>builder()
        .result(result)
        .message(ApiMessage.TOKEN_REFRESHED.getMessage())
        .build();
  }

  @GetMapping("/role/{role}/page")
  public ApiResponse<Page<UserResponse>> getAllUsersByRoleWithPagination(
      @PathVariable String role,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    return ApiResponse.<Page<UserResponse>>builder()
        .result(userService.getAllUsersByRoleWithPagination(role, page, size))
        .message(ApiMessage.ALL_USERS_RETRIEVED.getMessage())
        .build();
  }

  @GetMapping("/role/{role}/all")
  public ApiResponse<List<UserResponse>> getAllUsersByRole(@PathVariable String role) {
    return ApiResponse.<List<UserResponse>>builder()
        .result(userService.getAllUsersByRole(role))
        .message(ApiMessage.ALL_USERS_RETRIEVED.getMessage())
        .build();
  }

  @GetMapping("/inventory/{inventoryId}/managers/page")
  public ApiResponse<Page<UserResponse>> getManagersByInventoryIdWithPagination(
      @PathVariable Long inventoryId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    return ApiResponse.<Page<UserResponse>>builder()
        .result(userService.getManagersByInventoryIdWithPagination(inventoryId, page, size))
        .message(ApiMessage.ALL_USERS_RETRIEVED.getMessage())
        .build();
  }

  @GetMapping("/inventory/{inventoryId}/managers/all")
  public ApiResponse<List<UserResponse>> getAllManagersByInventoryId(
      @PathVariable Long inventoryId) {
    return ApiResponse.<List<UserResponse>>builder()
        .result(userService.getAllManagersByInventoryId(inventoryId))
        .message(ApiMessage.ALL_USERS_RETRIEVED.getMessage())
        .build();
  }
}
