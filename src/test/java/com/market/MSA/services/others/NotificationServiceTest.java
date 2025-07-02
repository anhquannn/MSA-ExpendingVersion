package com.market.MSA.services.others;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.market.MSA.exceptions.ErrorCode;
import com.market.MSA.mappers.others.NotificationMapper;
import com.market.MSA.models.others.Notification;
import com.market.MSA.models.user.User;
import com.market.MSA.repositories.order.OrderRepository;
import com.market.MSA.repositories.others.NotificationRepository;
import com.market.MSA.repositories.product.InventoryProductRepository;
import com.market.MSA.repositories.product.InventoryRepository;
import com.market.MSA.repositories.product.ProductRepository;
import com.market.MSA.repositories.user.UserRepository;
import com.market.MSA.requests.others.NotificationRequest;
import com.market.MSA.responses.others.NotificationResponse;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Simple unit-test for {@link NotificationService#createNotification(NotificationRequest)}
 *
 * <p>Verifies that: 1. Notification entity is saved via repository. 2. FcmService is called to push
 * the notification once the user exists.
 */
@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

  @Mock private NotificationRepository notificationRepository;

  @Mock private EntityFinderService entityFinderService;

  @Mock private NotificationMapper notificationMapper;

  @Mock private UserRepository userRepository;

  @Mock private OrderRepository orderRepository;

  @Mock private ProductRepository productRepository;

  @Mock private InventoryRepository inventoryRepository;

  @Mock private InventoryProductRepository inventoryProductRepository;

  @Mock private FcmService fcmService;

  @InjectMocks private NotificationService notificationService;

  private static final Long USER_ID = 1L;

  private User dummyUser;
  private Notification dummyNotification;
  private NotificationRequest request;

  @BeforeEach
  void setUp() {
    dummyUser = new User();
    // assuming User has setter
    dummyUser.setUserId(USER_ID);

    request =
        NotificationRequest.builder()
            .userId(USER_ID)
            .message("Test message")
            .notificationType("order_created")
            .notificationDate(LocalDateTime.now())
            .isRead(false)
            .build();

    dummyNotification = new Notification();
    dummyNotification.setNotificationId(123L);
    dummyNotification.setUser(dummyUser);
    dummyNotification.setMessage(request.getMessage());
    dummyNotification.setNotificationType(request.getNotificationType());
  }

  @Test
  void createNotification_shouldSaveAndPush() {
    // mapper converts request to entity
    when(notificationMapper.toNotification(any(NotificationRequest.class)))
        .thenReturn(dummyNotification);

    // find user by id
    when(entityFinderService.findByIdOrThrow(eq(userRepository), eq(USER_ID), any(ErrorCode.class)))
        .thenReturn(dummyUser);

    // repository.save returns the same entity with id set
    when(notificationRepository.save(any(Notification.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    // mapper back to response (not really asserted)
    when(notificationMapper.toNotificationResponse(any(Notification.class)))
        .thenReturn(mock(NotificationResponse.class));

    // Act
    notificationService.createNotification(request);

    // Assert interactions
    verify(notificationRepository, times(1)).save(any(Notification.class));
    verify(fcmService, times(1))
        .pushNotification(eq(USER_ID), anyString(), eq(request.getMessage()), anyMap());
  }
}
