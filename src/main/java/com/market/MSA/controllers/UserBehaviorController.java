package com.market.MSA.controllers;

import com.market.MSA.requests.UserBehaviorRequest;
import com.market.MSA.responses.ApiResponse;
import com.market.MSA.responses.UserBehaviorResponse;
import com.market.MSA.services.UserBehaviorService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user-behavior")
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserBehaviorController {

  UserBehaviorService userBehaviorService;

  // Tạo UserBehavior
  @PostMapping
  public ApiResponse<UserBehaviorResponse> createUserBehavior(
      @RequestBody @Valid UserBehaviorRequest request) {
    return ApiResponse.<UserBehaviorResponse>builder()
        .result(userBehaviorService.createUserBehavior(request))
        .build();
  }

  // Cập nhật UserBehavior
  @PutMapping("/{userBehaviorId}")
  public ApiResponse<UserBehaviorResponse> updateUserBehavior(
      @PathVariable long userBehaviorId, @RequestBody @Valid UserBehaviorRequest request) {
    return ApiResponse.<UserBehaviorResponse>builder()
        .result(userBehaviorService.updateUserBehavior(userBehaviorId, request))
        .build();
  }

  // Xóa UserBehavior
  @DeleteMapping("/{userBehaviorId}")
  public ApiResponse<Boolean> deleteUserBehavior(@PathVariable long userBehaviorId) {
    Boolean result = userBehaviorService.deleteUserBehavior(userBehaviorId);
    return ApiResponse.<Boolean>builder().result(result).build();
  }

  // Lấy UserBehavior theo ID
  @GetMapping("/{userBehaviorId}")
  public ApiResponse<UserBehaviorResponse> getUserBehaviorById(@PathVariable long userBehaviorId) {
    return ApiResponse.<UserBehaviorResponse>builder()
        .result(userBehaviorService.getUserBehaviorById(userBehaviorId))
        .build();
  }

  // Lấy tất cả UserBehavior (phân trang)
  @GetMapping
  public ApiResponse<List<UserBehaviorResponse>> getAllUserBehaviors(
      @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int pageSize) {
    return ApiResponse.<List<UserBehaviorResponse>>builder()
        .result(userBehaviorService.getAllUserBehaviors(page, pageSize))
        .build();
  }

  // Lấy UserBehavior theo userId (phân trang)
  @GetMapping("/user/{userId}")
  public ApiResponse<List<UserBehaviorResponse>> getUserBehaviorsByUserId(
      @PathVariable long userId,
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int pageSize) {
    return ApiResponse.<List<UserBehaviorResponse>>builder()
        .result(userBehaviorService.getUserBehaviorsByUserId(userId, page, pageSize))
        .build();
  }
}
