package com.market.MSA.controllers.user;

import com.market.MSA.constants.ApiMessage;
import com.market.MSA.requests.user.*;
import com.market.MSA.responses.others.ApiResponse;
import com.market.MSA.responses.user.AuthenticationResponse;
import com.market.MSA.responses.user.UserResponse;
import com.market.MSA.services.user.AuthenticationService;
import com.market.MSA.services.user.UserService;
import com.nimbusds.jose.JOSEException;
import jakarta.validation.Valid;
import java.text.ParseException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
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

  @PutMapping("/device/{deviceId}/{userId}")
  ApiResponse<UserResponse> updateDeviceId(
      @PathVariable String deviceId, @PathVariable long userId) {
    return ApiResponse.<UserResponse>builder()
        .result(userService.updateDeviceId(deviceId, userId))
        .message(ApiMessage.USER_UPDATED.getMessage())
        .build();
  }

  @PostMapping("/login")
  public ApiResponse<UserResponse> login(@RequestBody @Valid AuthenticationRequest request) {
    UserResponse userResponse = userService.validateCredentials(request);
    CompletableFuture.runAsync(() -> userService.sendLoginOtp(request.getEmail()));

    return ApiResponse.<UserResponse>builder()
        .result(userResponse)
        .message(ApiMessage.USER_LOGGED_IN.getMessage())
        .build();
  }

  @PostMapping("/admin/login")
  public ApiResponse<AuthenticationResponse> loginAdmin(
      @RequestBody @Valid AuthenticationRequest request) {
    if (request.getFcmToken() != null && request.getPlatform() != null) {
      return ApiResponse.<AuthenticationResponse>builder()
          .result(
              userService.loginAdmin(
                  request.getEmail(),
                  request.getPassword(),
                  request.getFcmToken(),
                  request.getPlatform()))
          .message(ApiMessage.USER_LOGGED_IN.getMessage())
          .build();
    }
    return ApiResponse.<AuthenticationResponse>builder()
        .result(userService.loginAdmin(request.getEmail(), request.getPassword()))
        .message(ApiMessage.USER_LOGGED_IN.getMessage())
        .build();
  }

  @PostMapping("/verify-otp")
  ApiResponse<AuthenticationResponse> verifyOtp(@RequestBody @Valid VerifyOtpRequest requests) {
    return ApiResponse.<AuthenticationResponse>builder()
        .result(userService.verifyOtp(requests.getOtp()))
        .message(ApiMessage.EMAIL_VERIFIED.getMessage())
        .build();
  }

  @PostMapping("/reset-password")
  ApiResponse<UserResponse> resetPassword(@RequestBody @Valid AuthenticationRequest request) {
    UserResponse userResponse = userService.existsByEmail(request.getEmail());
    CompletableFuture.runAsync(() -> userService.sendLoginOtp(request.getEmail()));

    return ApiResponse.<UserResponse>builder()
        .result(userResponse)
        .message(ApiMessage.PASSWORD_RESET.getMessage())
        .build();
  }

  @PostMapping("/resend")
  ApiResponse<UserResponse> resendOtp(@RequestBody @Valid AuthenticationRequest request) {
    UserResponse userResponse = userService.existsByEmail(request.getEmail());
    CompletableFuture.runAsync(() -> userService.resendOTP(request.getEmail()));

    return ApiResponse.<UserResponse>builder()
        .result(userResponse)
        .message(ApiMessage.PASSWORD_RESET.getMessage())
        .build();
  }

  @PostMapping("/login/google")
  ApiResponse<AuthenticationResponse> loginWithGoogle(
      @RequestBody @Valid GoogleLoginRequest request) {
    return ApiResponse.<AuthenticationResponse>builder()
        .result(userService.loginWithGoogle(request.getAccessToken()))
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

  @PutMapping("/admin/{userId}")
  ApiResponse<UserResponse> updateUserWithoutPassword(
      @PathVariable long userId, @RequestBody UpdateUserRequest request) {
    return ApiResponse.<UserResponse>builder()
        .result(userService.updateUserWithoutPassword(userId, request))
        .message(ApiMessage.USER_UPDATED.getMessage())
        .build();
  }

  @PutMapping("/password/{userId}")
  ApiResponse<UserResponse> changePassword(
      @PathVariable long userId, @RequestBody UpdatePasswordRequest request) {
    return ApiResponse.<UserResponse>builder()
        .result(
            userService.changePassword(userId, request.getOldPassword(), request.getNewPassword()))
        .message(ApiMessage.USER_UPDATED.getMessage())
        .build();
  }

  @PutMapping("/profile/{userId}")
  ApiResponse<UserResponse> updateProfile(
      @PathVariable long userId, @RequestBody UpdateUserRequest request) {
    return ApiResponse.<UserResponse>builder()
        .result(userService.updateProfile(userId, request))
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
  ApiResponse<AuthenticationResponse> refresh(@RequestBody @Valid RefreshRequest request)
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
