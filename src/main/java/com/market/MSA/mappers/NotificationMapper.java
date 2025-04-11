package com.market.MSA.mappers;

import com.market.MSA.models.Notification;
import com.market.MSA.requests.NotificationRequest;
import com.market.MSA.responses.NotificationResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
@Component
public interface NotificationMapper {
  Notification toNotification(NotificationRequest request);

  NotificationResponse toNotificationResponse(Notification notification);

  @Mapping(target = "notificationId", ignore = true)
  void updateNotification(NotificationRequest request, @MappingTarget Notification notification);
}
