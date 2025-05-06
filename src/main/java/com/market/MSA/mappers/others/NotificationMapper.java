package com.market.MSA.mappers.others;

import com.market.MSA.mappers.product.InventoryMapper;
import com.market.MSA.models.others.Notification;
import com.market.MSA.requests.others.NotificationRequest;
import com.market.MSA.responses.others.NotificationResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.stereotype.Component;

@Mapper(
    componentModel = "spring",
    uses = {InventoryMapper.class})
@Component
public interface NotificationMapper {
  Notification toNotification(NotificationRequest request);

  @Mapping(target = "isRead", source = "read")
  NotificationResponse toNotificationResponse(Notification notification);

  @Mapping(target = "notificationId", ignore = true)
  void updateNotification(NotificationRequest request, @MappingTarget Notification notification);
}
