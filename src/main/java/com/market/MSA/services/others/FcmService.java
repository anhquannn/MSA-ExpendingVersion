package com.market.MSA.services.others;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;
import com.market.MSA.models.others.DeviceToken;
import com.market.MSA.models.others.Platform;
import com.market.MSA.repositories.others.DeviceTokenRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class FcmService {

  private final DeviceTokenRepository deviceTokenRepository;

  /** Lưu hoặc cập nhật token cho user. */
  @Transactional
  public void registerToken(Long userId, String token, Platform platform) {
    deviceTokenRepository
        .findByToken(token)
        .ifPresentOrElse(
            dt -> {
              dt.setUserId(userId);
              dt.setPlatform(platform);
            },
            () ->
                deviceTokenRepository.save(
                    DeviceToken.builder()
                        .userId(userId)
                        .token(token)
                        .platform(platform)
                        .createdAt(LocalDateTime.now())
                        .build()));
  }

  /** Gửi thông báo tới một user. */
  public void pushNotification(Long toUserId, String title, String body, Map<String, String> data) {
    List<String> tokens =
        deviceTokenRepository.findAllByUserId(toUserId).stream()
            .map(DeviceToken::getToken)
            .toList();
    if (tokens.isEmpty()) {
      log.warn("No device tokens for user {}", toUserId);
      return;
    }
    try {
      MulticastMessage message =
          MulticastMessage.builder()
              .addAllTokens(tokens)
              .putAllData(data == null ? Map.of() : data)
              .setNotification(Notification.builder().setTitle(title).setBody(body).build())
              .build();
      FirebaseMessaging.getInstance().sendMulticast(message);
      log.info("Sent FCM notification to user {} with {} tokens", toUserId, tokens.size());
    } catch (Exception ex) {
      log.error("Error sending FCM", ex);
    }
  }
}
