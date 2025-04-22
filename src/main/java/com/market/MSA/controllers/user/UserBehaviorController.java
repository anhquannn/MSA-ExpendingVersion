package com.market.MSA.controllers.user;

import com.market.MSA.constants.ApiMessage;
import com.market.MSA.requests.user.UserBehaviorRequest;
import com.market.MSA.responses.others.ApiResponse;
import com.market.MSA.responses.user.UserBehaviorResponse;
import com.market.MSA.services.user.UserBehaviorService;
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
        .message(ApiMessage.USER_BEHAVIOR_CREATED.getMessage())
        .build();
  }

  // Cập nhật UserBehavior
  @PutMapping("/{userBehaviorId}")
  public ApiResponse<UserBehaviorResponse> updateUserBehavior(
      @PathVariable long userBehaviorId, @RequestBody @Valid UserBehaviorRequest request) {
    return ApiResponse.<UserBehaviorResponse>builder()
        .result(userBehaviorService.updateUserBehavior(userBehaviorId, request))
        .message(ApiMessage.USER_BEHAVIOR_UPDATED.getMessage())
        .build();
  }

  // Xóa UserBehavior
  @DeleteMapping("/{userBehaviorId}")
  public ApiResponse<Boolean> deleteUserBehavior(@PathVariable long userBehaviorId) {
    Boolean result = userBehaviorService.deleteUserBehavior(userBehaviorId);
    return ApiResponse.<Boolean>builder()
        .result(result)
        .message(ApiMessage.USER_BEHAVIOR_DELETED.getMessage())
        .build();
  }

  // Lấy UserBehavior theo ID
  @GetMapping("/{userBehaviorId}")
  public ApiResponse<UserBehaviorResponse> getUserBehaviorById(@PathVariable long userBehaviorId) {
    return ApiResponse.<UserBehaviorResponse>builder()
        .result(userBehaviorService.getUserBehaviorById(userBehaviorId))
        .message(ApiMessage.USER_BEHAVIOR_RETRIEVED.getMessage())
        .build();
  }

  // Lấy tất cả UserBehavior (phân trang)
  @GetMapping
  public ApiResponse<List<UserBehaviorResponse>> getAllUserBehaviors(
      @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int pageSize) {
    return ApiResponse.<List<UserBehaviorResponse>>builder()
        .result(userBehaviorService.getAllUserBehaviors(page, pageSize))
        .message(ApiMessage.ALL_USER_BEHAVIORS_RETRIEVED.getMessage())
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
        .message(ApiMessage.ALL_USER_BEHAVIORS_RETRIEVED.getMessage())
        .build();
  }
}
