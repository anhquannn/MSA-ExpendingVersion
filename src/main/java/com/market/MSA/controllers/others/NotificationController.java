package com.market.MSA.controllers.others;

import com.market.MSA.constants.ApiMessage;
import com.market.MSA.requests.filters.NotificationFilterRequest;
import com.market.MSA.requests.others.NotificationRequest;
import com.market.MSA.requests.others.PushRequest;
import com.market.MSA.requests.others.RegisterTokenRequest;
import com.market.MSA.responses.others.ApiResponse;
import com.market.MSA.responses.others.NotificationResponse;
import com.market.MSA.services.others.FcmService;
import com.market.MSA.services.others.NotificationService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notification")
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationController {

  NotificationService notificationService;
  FcmService fcmService;

  @PostMapping("/register-token")
  public ApiResponse<Void> registerToken(
      @RequestBody RegisterTokenRequest request,
      @AuthenticationPrincipal(expression = "userId") Long userId) {
    fcmService.registerToken(userId, request.token(), request.platform());
    return ApiResponse.<Void>builder().message("Token registered").build();
  }

  @PostMapping("/push-test")
  public ApiResponse<Void> pushTest(@RequestBody PushRequest request) {
    fcmService.pushNotification(request.userId(), request.title(), request.body(), request.data());
    return ApiResponse.<Void>builder().message("Pushed").build();
  }

  @PostMapping
  public ApiResponse<NotificationResponse> createNotification(
      @RequestBody NotificationRequest request) {
    return ApiResponse.<NotificationResponse>builder()
        .result(notificationService.createNotification(request))
        .message(ApiMessage.NOTIFICATION_CREATED.getMessage())
        .build();
  }

  @PutMapping("/{id}")
  public ApiResponse<NotificationResponse> updateNotification(
      @PathVariable Long id, @RequestBody NotificationRequest request) {
    return ApiResponse.<NotificationResponse>builder()
        .result(notificationService.updateNotification(id, request))
        .message(ApiMessage.NOTIFICATION_UPDATED.getMessage())
        .build();
  }

  @DeleteMapping("/{id}")
  public ApiResponse<Boolean> deleteNotification(@PathVariable Long id) {
    return ApiResponse.<Boolean>builder()
        .result(notificationService.deleteNotification(id))
        .message(ApiMessage.NOTIFICATION_DELETED.getMessage())
        .build();
  }

  @GetMapping("/{id}")
  public ApiResponse<NotificationResponse> getNotificationById(@PathVariable Long id) {
    return ApiResponse.<NotificationResponse>builder()
        .result(notificationService.getNotificationById(id))
        .message(ApiMessage.NOTIFICATION_RETRIEVED.getMessage())
        .build();
  }

  @GetMapping
  public ApiResponse<List<NotificationResponse>> getAll() {
    return ApiResponse.<List<NotificationResponse>>builder()
        .result(notificationService.getAll())
        .message(ApiMessage.ALL_NOTIFICATIONS_RETRIEVED.getMessage())
        .build();
  }

  @PostMapping("/list")
  public ApiResponse<List<NotificationResponse>> filterNotifications(
      @Valid @RequestBody NotificationFilterRequest request) {
    return ApiResponse.<List<NotificationResponse>>builder()
        .result(notificationService.getAllNotifications(request))
        .message(ApiMessage.ALL_NOTIFICATIONS_RETRIEVED.getMessage())
        .build();
  }

  @PostMapping("/paging")
  public ApiResponse<Page<NotificationResponse>> filterNotificationsWithPaging(
      @Valid @RequestBody NotificationFilterRequest request) {
    return ApiResponse.<Page<NotificationResponse>>builder()
        .result(notificationService.getAllNotificationsWithPaging(request))
        .message(ApiMessage.ALL_NOTIFICATIONS_RETRIEVED.getMessage())
        .build();
  }
}
