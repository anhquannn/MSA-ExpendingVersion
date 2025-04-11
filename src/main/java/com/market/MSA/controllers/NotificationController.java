package com.market.MSA.controllers;

import com.market.MSA.requests.NotificationRequest;
import com.market.MSA.responses.ApiResponse;
import com.market.MSA.responses.NotificationResponse;
import com.market.MSA.services.NotificationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notification")
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationController {

  NotificationService notificationService;

  @PostMapping
  public ApiResponse<NotificationResponse> createNotification(
      @RequestBody NotificationRequest request) {
    return ApiResponse.<NotificationResponse>builder()
        .result(notificationService.createNotification(request))
        .build();
  }

  @PutMapping("/{id}")
  public ApiResponse<NotificationResponse> updateNotification(
      @PathVariable Long id, @RequestBody NotificationRequest request) {
    return ApiResponse.<NotificationResponse>builder()
        .result(notificationService.updateNotification(id, request))
        .build();
  }

  @DeleteMapping("/{id}")
  public ApiResponse<Boolean> deleteNotification(@PathVariable Long id) {
    return ApiResponse.<Boolean>builder()
        .result(notificationService.deleteNotification(id))
        .build();
  }

  @GetMapping("/{id}")
  public ApiResponse<NotificationResponse> getNotificationById(@PathVariable Long id) {
    return ApiResponse.<NotificationResponse>builder()
        .result(notificationService.getNotificationById(id))
        .build();
  }

  @GetMapping("/user/{userId}")
  public ApiResponse<Page<NotificationResponse>> getAllByUserId(
      @PathVariable Long userId,
      @RequestParam(required = false) String type,
      @RequestParam(required = false) Boolean isRead,
      @RequestParam(required = false) Long productId,
      @RequestParam(required = false) Long orderId,
      @RequestParam(required = false) Long inventoryId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    return ApiResponse.<Page<NotificationResponse>>builder()
        .result(
            notificationService.getAllByUserId(
                userId, type, isRead, productId, orderId, inventoryId, page, size))
        .build();
  }

  @GetMapping
  public ApiResponse<Page<NotificationResponse>> getAllNotifications(
      @RequestParam(required = false) String type,
      @RequestParam(required = false) Boolean isRead,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    return ApiResponse.<Page<NotificationResponse>>builder()
        .result(notificationService.getAllNotifications(type, isRead, page, size))
        .build();
  }
}
