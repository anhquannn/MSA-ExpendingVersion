package com.market.MSA.controllers.user;

import com.market.MSA.constants.ApiMessage;
import com.market.MSA.requests.filters.UserBehaviorFilterRequest;
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
import org.springframework.data.domain.Page;
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

  @GetMapping
  public ApiResponse<List<UserBehaviorResponse>> getAll() {
    return ApiResponse.<List<UserBehaviorResponse>>builder()
        .result(userBehaviorService.getAll())
        .message(ApiMessage.ALL_USER_BEHAVIORS_RETRIEVED.getMessage())
        .build();
  }

  @PostMapping("/list")
  public ApiResponse<List<UserBehaviorResponse>> filterUserBehaviors(
      @Valid @RequestBody UserBehaviorFilterRequest request) {
    return ApiResponse.<List<UserBehaviorResponse>>builder()
        .result(userBehaviorService.getAllUserBehaviors(request))
        .message(ApiMessage.ALL_USER_BEHAVIORS_RETRIEVED.getMessage())
        .build();
  }

  @PostMapping("/paging")
  public ApiResponse<Page<UserBehaviorResponse>> filterUserBehaviorsWithPaging(
      @Valid @RequestBody UserBehaviorFilterRequest request) {
    return ApiResponse.<Page<UserBehaviorResponse>>builder()
        .result(userBehaviorService.getAllUserBehaviorsWithPaging(request))
        .message(ApiMessage.ALL_USER_BEHAVIORS_RETRIEVED.getMessage())
        .build();
  }
}
