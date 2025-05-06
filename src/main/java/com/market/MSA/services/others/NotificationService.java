package com.market.MSA.services.others;

import com.market.MSA.exceptions.AppException;
import com.market.MSA.exceptions.ErrorCode;
import com.market.MSA.mappers.others.NotificationMapper;
import com.market.MSA.models.order.Order;
import com.market.MSA.models.others.Notification;
import com.market.MSA.models.product.Inventory;
import com.market.MSA.models.product.InventoryProduct;
import com.market.MSA.models.product.Product;
import com.market.MSA.models.user.User;
import com.market.MSA.repositories.order.OrderRepository;
import com.market.MSA.repositories.others.NotificationRepository;
import com.market.MSA.repositories.product.InventoryProductRepository;
import com.market.MSA.repositories.product.InventoryRepository;
import com.market.MSA.repositories.product.ProductRepository;
import com.market.MSA.repositories.user.UserRepository;
import com.market.MSA.requests.others.NotificationRequest;
import com.market.MSA.responses.others.NotificationResponse;
import java.util.Date;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationService {
  final NotificationRepository notificationRepository;
  final EntityFinderService entityFinderService;
  final NotificationMapper notificationMapper;
  final UserRepository userRepository;
  final OrderRepository orderRepository;
  final ProductRepository productRepository;
  final InventoryRepository inventoryRepository;
  final InventoryProductRepository inventoryProductRepository;

  @Transactional
  public NotificationResponse createNotification(NotificationRequest notificationRequest) {
    Notification notification = notificationMapper.toNotification(notificationRequest);
    if (notificationRequest.getUserId() != null) {
      notification.setUser(
          entityFinderService.findByIdOrThrow(
              userRepository, notificationRequest.getUserId(), ErrorCode.USER_NOT_EXISTED));
    } else {
      notification.setUser(null);
    }

    if (notificationRequest.getOrderId() != null) {
      notification.setOrder(
          entityFinderService.findByIdOrThrow(
              orderRepository, notificationRequest.getOrderId(), ErrorCode.ORDER_NOT_FOUND));
    } else {
      notification.setOrder(null);
    }

    if (notificationRequest.getProductId() != null) {
      notification.setProduct(
          entityFinderService.findByIdOrThrow(
              productRepository, notificationRequest.getProductId(), ErrorCode.PRODUCT_NOT_FOUND));
    } else {
      notification.setProduct(null);
    }

    if (notificationRequest.getInventoryId() != null) {
      notification.setInventory(
          entityFinderService.findByIdOrThrow(
              inventoryRepository,
              notificationRequest.getInventoryId(),
              ErrorCode.INVENTORY_NOT_FOUND));
    } else {
      notification.setInventory(null);
    }

    notification = notificationRepository.save(notification);
    return notificationMapper.toNotificationResponse(notification);
  }

  @Transactional
  public NotificationResponse updateNotification(
      Long notificationId, NotificationRequest notificationRequest) {
    Notification notification =
        notificationRepository
            .findById(notificationId)
            .orElseThrow(() -> new AppException(ErrorCode.NOTIFICATION_NOT_FOUND));
    if (notificationRequest.getUserId() != null) {
      notification.setUser(
          entityFinderService.findByIdOrThrow(
              userRepository, notificationRequest.getUserId(), ErrorCode.USER_NOT_EXISTED));
    } else {
      notification.setUser(null);
    }

    if (notificationRequest.getOrderId() != null) {
      notification.setOrder(
          entityFinderService.findByIdOrThrow(
              orderRepository, notificationRequest.getOrderId(), ErrorCode.ORDER_NOT_FOUND));
    } else {
      notification.setOrder(null);
    }

    if (notificationRequest.getProductId() != null) {
      notification.setProduct(
          entityFinderService.findByIdOrThrow(
              productRepository, notificationRequest.getProductId(), ErrorCode.PRODUCT_NOT_FOUND));
    } else {
      notification.setProduct(null);
    }

    if (notificationRequest.getInventoryId() != null) {
      notification.setInventory(
          entityFinderService.findByIdOrThrow(
              inventoryRepository,
              notificationRequest.getInventoryId(),
              ErrorCode.INVENTORY_NOT_FOUND));
    } else {
      notification.setInventory(null);
    }

    notificationMapper.updateNotification(notificationRequest, notification);
    Notification updatedNotification = notificationRepository.save(notification);

    return notificationMapper.toNotificationResponse(updatedNotification);
  }

  @Transactional
  public boolean deleteNotification(Long notificationId) {
    if (!notificationRepository.existsById(notificationId)) {
      throw new AppException(ErrorCode.NOTIFICATION_NOT_FOUND);
    }
    notificationRepository.deleteById(notificationId);
    return true;
  }

  public NotificationResponse getNotificationById(Long notificationId) {
    return notificationRepository
        .findById(notificationId)
        .map(notificationMapper::toNotificationResponse)
        .orElseThrow(() -> new AppException(ErrorCode.NOTIFICATION_NOT_FOUND));
  }

  @Cacheable(
      value = "notifications",
      key =
          "'user_' + #userId + '_' + #type + '_' + #isRead + '_' + #productId + '_' + #orderId + '_' + #inventoryId + '_' + #page + '_' + #size")
  public Page<NotificationResponse> getAllByUserId(
      Long userId,
      String type,
      Boolean isRead,
      Long productId,
      Long orderId,
      Long inventoryId,
      int page,
      int size) {
    // Check if user exists
    if (!userRepository.existsById(userId)) {
      throw new AppException(ErrorCode.USER_NOT_EXISTED);
    }

    // Create pageable
    Pageable pageable = PageRequest.of(page, size);

    // Get notifications with filters
    Page<Notification> notifications =
        notificationRepository.findAllByUserIdWithFilters(
            userId, type, isRead, productId, orderId, inventoryId, pageable);

    return notifications.map(notificationMapper::toNotificationResponse);
  }

  @Cacheable(
      value = "notifications",
      key = "'all_' + #type + '_' + #isRead + '_' + #page + '_' + #size")
  public Page<NotificationResponse> getAllNotifications(
      String type, Boolean isRead, int page, int size) {
    // Create pageable
    Pageable pageable = PageRequest.of(page, size);

    // Get notifications with filters
    Page<Notification> notifications =
        notificationRepository.findAllWithFilters(type, isRead, pageable);

    return notifications.map(notificationMapper::toNotificationResponse);
  }

  @Transactional
  public void sendProductNotificationToAllCustomers(Long productId, boolean sendToAll) {
    if (!sendToAll) {
      return;
    }

    // Get product
    Product product =
        entityFinderService.findByIdOrThrow(
            productRepository, productId, ErrorCode.PRODUCT_NOT_FOUND);

    // Get all customers
    List<User> customers = userRepository.findAllByRole("CUSTOMER");

    // Create notification for each customer
    for (User customer : customers) {
      NotificationRequest request =
          NotificationRequest.builder()
              .userId(customer.getUserId())
              .productId(productId)
              .message("Sản phẩm " + product.getName() + " đã được thêm vào cửa hàng")
              .notificationType("product_new")
              .notificationDate(new Date())
              .isRead(false)
              .build();

      createNotification(request);
    }
  }

  @Transactional
  public void sendOrderCreatedNotification(Long orderId) {
    // Get order
    Order order =
        entityFinderService.findByIdOrThrow(orderRepository, orderId, ErrorCode.ORDER_NOT_FOUND);

    // Create notification for customer
    NotificationRequest request =
        NotificationRequest.builder()
            .userId(order.getUser().getUserId())
            .orderId(orderId)
            .message("Đơn hàng #" + orderId + " của bạn đã được tạo thành công")
            .notificationType("order_created")
            .notificationDate(new Date())
            .isRead(false)
            .build();

    createNotification(request);
  }

  @Transactional
  public void sendOrderCancelledNotification(Long orderId) {
    // Get order
    Order order =
        entityFinderService.findByIdOrThrow(orderRepository, orderId, ErrorCode.ORDER_NOT_FOUND);

    // Create notification for customer
    NotificationRequest request =
        NotificationRequest.builder()
            .userId(order.getUser().getUserId())
            .orderId(orderId)
            .message("Đơn hàng #" + orderId + " của bạn đã bị hủy")
            .notificationType("order_cancelled")
            .notificationDate(new Date())
            .isRead(false)
            .build();

    createNotification(request);
  }

  @Transactional
  public void sendLowStockNotification(
      Long inventoryId, Long productId, int currentStock, int threshold) {
    // Get inventory and product
    Inventory inventory =
        entityFinderService.findByIdOrThrow(
            inventoryRepository, inventoryId, ErrorCode.INVENTORY_NOT_FOUND);
    Product product =
        entityFinderService.findByIdOrThrow(
            productRepository, productId, ErrorCode.PRODUCT_NOT_FOUND);

    // Get managers for this inventory
    List<User> managers = userRepository.findAllManagersByInventoryId(inventoryId);

    // Create notification for each manager
    for (User manager : managers) {
      NotificationRequest request =
          NotificationRequest.builder()
              .userId(manager.getUserId())
              .inventoryId(inventoryId)
              .message(
                  "Sản phẩm "
                      + product.getName()
                      + " tại "
                      + inventory.getBranch().getName()
                      + " chỉ còn "
                      + currentStock
                      + " sản phẩm (ngưỡng cảnh báo: "
                      + threshold
                      + ")")
              .notificationType("low_stock")
              .notificationDate(new Date())
              .isRead(false)
              .build();

      createNotification(request);
    }
  }

  @Transactional
  public void checkAndNotifyLowStock() {
    // Default threshold for low stock warning
    final int DEFAULT_LOW_STOCK_THRESHOLD = 10;

    // Get all inventories
    List<Inventory> inventories = inventoryRepository.findAll();

    // Check each inventory
    for (Inventory inventory : inventories) {
      // Get products in this inventory
      List<InventoryProduct> inventoryProducts =
          inventoryProductRepository.findAllByInventory_InventoryId(inventory.getInventoryId());

      // Check each product
      for (InventoryProduct inventoryProduct : inventoryProducts) {
        // Use default threshold

        // Get total stock in branch using the new method
        int totalStock =
            inventoryProductRepository.getTotalStockByBranchAndProduct(
                inventory.getBranch().getBranchId(), inventoryProduct.getProduct().getProductId());

        // If stock is below threshold, send notification
        if (totalStock <= DEFAULT_LOW_STOCK_THRESHOLD) {
          sendLowStockNotification(
              inventory.getInventoryId(),
              inventoryProduct.getProduct().getProductId(),
              totalStock,
              DEFAULT_LOW_STOCK_THRESHOLD);
        }
      }
    }
  }
}
