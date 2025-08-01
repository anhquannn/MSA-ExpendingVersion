package com.market.MSA.services.others;

import com.market.MSA.exceptions.AppException;
import com.market.MSA.exceptions.ErrorCode;
import com.market.MSA.mappers.others.NotificationMapper;
import com.market.MSA.models.order.Order;
import com.market.MSA.models.others.Notification;
import com.market.MSA.models.product.Inventory;
import com.market.MSA.models.product.InventoryProduct;
import com.market.MSA.models.product.Product;
import com.market.MSA.models.product.Transfer;
import com.market.MSA.models.user.User;
import com.market.MSA.repositories.order.OrderRepository;
import com.market.MSA.repositories.others.NotificationRepository;
import com.market.MSA.repositories.product.InventoryProductRepository;
import com.market.MSA.repositories.product.InventoryRepository;
import com.market.MSA.repositories.product.ProductRepository;
import com.market.MSA.repositories.product.TransferRequestRepository;
import com.market.MSA.repositories.user.UserRepository;
import com.market.MSA.requests.filters.NotificationFilterRequest;
import com.market.MSA.requests.others.NotificationRequest;
import com.market.MSA.responses.others.NotificationResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
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
  final TransferRequestRepository transferRequestRepository;
  final FcmService fcmService;

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

    // Push via FCM
    pushToUser(notification);
    return notificationMapper.toNotificationResponse(notification);
  }

  @Transactional
  public NotificationResponse updateNotification(
      Long notificationId, NotificationRequest notificationRequest) {
    Notification notification =
        notificationRepository
            .findById(notificationId)
            .orElseThrow(() -> new AppException(ErrorCode.NOTIFICATION_NOT_FOUND));

    // Only update relationships if the corresponding ID is provided in the request
    if (notificationRequest.getUserId() != null) {
      notification.setUser(
          entityFinderService.findByIdOrThrow(
              userRepository, notificationRequest.getUserId(), ErrorCode.USER_NOT_EXISTED));
    }

    if (notificationRequest.getOrderId() != null) {
      notification.setOrder(
          entityFinderService.findByIdOrThrow(
              orderRepository, notificationRequest.getOrderId(), ErrorCode.ORDER_NOT_FOUND));
    }

    if (notificationRequest.getProductId() != null) {
      notification.setProduct(
          entityFinderService.findByIdOrThrow(
              productRepository, notificationRequest.getProductId(), ErrorCode.PRODUCT_NOT_FOUND));
    }

    if (notificationRequest.getInventoryId() != null) {
      notification.setInventory(
          entityFinderService.findByIdOrThrow(
              inventoryRepository,
              notificationRequest.getInventoryId(),
              ErrorCode.INVENTORY_NOT_FOUND));
    }

    notificationMapper.updateNotification(notificationRequest, notification);
    notification.setRead(notificationRequest.isRead());

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

  @Cacheable("all_notifications")
  public List<NotificationResponse> getAll() {
    return notificationRepository.findAll().stream()
        .map(notificationMapper::toNotificationResponse)
        .collect(Collectors.toList());
  }

  @Cacheable("notifications_list")
  @Transactional(readOnly = true)
  public List<NotificationResponse> getAllNotifications(NotificationFilterRequest request) {
    // Handle date range
    LocalDateTime fromDate = request.getFromDate();
    LocalDateTime toDate = request.getToDate();

    // If only one date is provided, set a default range
    if (fromDate != null && toDate == null) {
      toDate = LocalDateTime.now();
    } else if (fromDate == null && toDate != null) {
      fromDate = toDate.minusMonths(1); // Default to last month if only toDate is provided
    }

    Sort sort = Sort.by(Sort.Direction.fromString(request.getSortDirection()), request.getSortBy());

    if (request.getUserId() != null) {
      // Filter with userId
      return notificationRepository
          .findAllByUserIdWithFiltersNoPaging(
              request.getUserId(),
              request.getType(),
              request.getIsRead(),
              request.getProductId(),
              request.getOrderId(),
              request.getInventoryId(),
              fromDate,
              toDate,
              sort)
          .stream()
          .map(notificationMapper::toNotificationResponse)
          .collect(Collectors.toList());
    } else {
      // Filter without userId
      return notificationRepository
          .findAllWithFiltersNoPaging(
              request.getType(), request.getIsRead(), fromDate, toDate, sort)
          .stream()
          .map(notificationMapper::toNotificationResponse)
          .collect(Collectors.toList());
    }
  }

  @Cacheable("notifications_paging")
  @Transactional(readOnly = true)
  public Page<NotificationResponse> getAllNotificationsWithPaging(
      NotificationFilterRequest request) {
    // Handle date range
    LocalDateTime fromDate = request.getFromDate();
    LocalDateTime toDate = request.getToDate();

    // If only one date is provided, set a default range
    if (fromDate != null && toDate == null) {
      toDate = LocalDateTime.now();
    } else if (fromDate == null && toDate != null) {
      fromDate = toDate.minusMonths(1); // Default to last month if only toDate is provided
    }

    Sort sort = Sort.by(Sort.Direction.fromString(request.getSortDirection()), request.getSortBy());

    Pageable pageable =
        PageRequest.of(
            request.getPage() - 1, // Convert to 0-based page index
            request.getPageSize(),
            sort);

    if (request.getUserId() != null) {
      // Filter with userId
      return notificationRepository
          .filterWithPaging(
              request.getUserId(),
              request.getType(),
              request.getIsRead(),
              request.getProductId(),
              request.getOrderId(),
              request.getInventoryId(),
              fromDate,
              toDate,
              pageable)
          .map(notificationMapper::toNotificationResponse);
    } else {
      // Filter without userId
      return notificationRepository
          .filter(request.getType(), request.getIsRead(), fromDate, toDate, pageable)
          .map(notificationMapper::toNotificationResponse);
    }
  }

  @Async("emailTaskExecutor")
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
              .notificationDate(LocalDateTime.now())
              .isRead(false)
              .build();

      createNotification(request);
    }
  }

  @Async("emailTaskExecutor")
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
            .notificationDate(LocalDateTime.now())
            .isRead(false)
            .build();

    createNotification(request);
  }

  @Async("emailTaskExecutor")
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
            .notificationDate(LocalDateTime.now())
            .isRead(false)
            .build();

    createNotification(request);
  }

  @Async("emailTaskExecutor")
  @Transactional
  public void notifyUser(Long userId, String message) {
    NotificationRequest req =
        NotificationRequest.builder()
            .userId(userId)
            .message(message)
            .notificationDate(LocalDateTime.now())
            .isRead(false)
            .build();
    createNotification(req);
  }

  @Async("emailTaskExecutor")
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
              .notificationDate(LocalDateTime.now())
              .isRead(false)
              .build();

      createNotification(request);
    }
  }

  @Async("emailTaskExecutor")
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
          inventoryProductRepository.filter(
              null, inventory.getInventoryId(), null, null, null, null, null, null);

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

  // ================= Transfer Notification Methods =================

  /**
   * Send notification when a transfer request is created Notifies both admin (approver) and manager
   * (requester)
   */
  @Async("emailTaskExecutor")
  @Transactional
  public void sendTransferCreatedNotification(Long transferId) {
    try {
      Transfer transfer =
          entityFinderService.findByIdOrThrow(
              transferRequestRepository, transferId, ErrorCode.TRANSFER_REQUEST_NOT_FOUND);

      // Notify admin (approver)
      if (transfer.getApprover() != null) {
        NotificationRequest adminRequest =
            NotificationRequest.builder()
                .userId(transfer.getApprover().getUserId())
                .message(
                    String.format(
                        "📋 Yêu cầu chuyển kho mới: Có yêu cầu chuyển kho #%d từ %s đến %s với %d sản phẩm cần duyệt.",
                        transfer.getTransferRequestId(),
                        transfer.getFromInventory().getName(),
                        transfer.getToInventory().getName(),
                        transfer.getTransferItems().size()))
                .notificationType("TRANSFER_PENDING")
                .notificationDate(LocalDateTime.now())
                .isRead(false)
                .build();
        createNotification(adminRequest);
      }

      // Notify manager (requester)
      if (transfer.getRequester() != null) {
        assert transfer.getApprover() != null;
        if (!transfer.getRequester().getUserId().equals(transfer.getApprover().getUserId())) {
          NotificationRequest managerRequest =
              NotificationRequest.builder()
                  .userId(transfer.getRequester().getUserId())
                  .message(
                      String.format(
                          "📤 Yêu cầu chuyển kho đã tạo: Yêu cầu chuyển kho #%d của bạn từ %s đến %s đã được tạo và đang chờ duyệt.",
                          transfer.getTransferRequestId(),
                          transfer.getFromInventory().getName(),
                          transfer.getToInventory().getName()))
                  .notificationType("TRANSFER_CREATED")
                  .notificationDate(LocalDateTime.now())
                  .isRead(false)
                  .build();
          createNotification(managerRequest);
        }
      }
    } catch (Exception ignored) {
    }
  }

  /** Send notification when a transfer request is approved Notifies the manager (requester) */
  @Async("emailTaskExecutor")
  @Transactional
  public void sendTransferApprovedNotification(Long transferId) {
    try {
      Transfer transfer =
          entityFinderService.findByIdOrThrow(
              transferRequestRepository, transferId, ErrorCode.TRANSFER_REQUEST_NOT_FOUND);

      if (transfer.getRequester() != null) {
        NotificationRequest request =
            NotificationRequest.builder()
                .userId(transfer.getRequester().getUserId())
                .message(
                    String.format(
                        "✅ Yêu cầu chuyển kho đã duyệt: Yêu cầu chuyển kho #%d của bạn từ %s đến %s đã được duyệt. Hàng hóa sẽ được chuyển sớm.",
                        transfer.getTransferRequestId(),
                        transfer.getFromInventory().getName(),
                        transfer.getToInventory().getName()))
                .notificationType("TRANSFER_APPROVED")
                .notificationDate(LocalDateTime.now())
                .isRead(false)
                .build();
        createNotification(request);
      }
    } catch (Exception ignored) {
    }
  }

  /** Send notification when a transfer request is rejected Notifies the manager (requester) */
  @Async("emailTaskExecutor")
  @Transactional
  public void sendTransferRejectedNotification(Long transferId, String reason) {
    try {
      Transfer transfer =
          entityFinderService.findByIdOrThrow(
              transferRequestRepository, transferId, ErrorCode.TRANSFER_REQUEST_NOT_FOUND);

      if (transfer.getRequester() != null) {
        String message =
            String.format(
                "❌ Yêu cầu chuyển kho bị từ chối: Yêu cầu chuyển kho #%d của bạn từ %s đến %s đã bị từ chối.",
                transfer.getTransferRequestId(),
                transfer.getFromInventory().getName(),
                transfer.getToInventory().getName());

        if (reason != null && !reason.trim().isEmpty()) {
          message += " Lý do: " + reason;
        }

        NotificationRequest request =
            NotificationRequest.builder()
                .userId(transfer.getRequester().getUserId())
                .message(message)
                .notificationType("TRANSFER_REJECTED")
                .notificationDate(LocalDateTime.now())
                .isRead(false)
                .build();
        createNotification(request);
      }
    } catch (Exception ignored) {
    }
  }

  /** Send notification when a return order is created - Notifies admin and branch manager */
  @Async("emailTaskExecutor")
  @Transactional
  public void sendReturnOrderCreatedNotification(Long returnOrderId, Long branchId) {
    try {
      // Notify admin (userId = 1)
      NotificationRequest adminRequest =
          NotificationRequest.builder()
              .userId(1L) // Admin user ID
              .message(
                  String.format(
                      "📦 Yêu cầu trả hàng mới: Khách hàng đã tạo yêu cầu trả hàng #%d. Vui lòng xem xét và xử lý.",
                      returnOrderId))
              .notificationType("RETURN_ORDER_CREATED")
              .notificationDate(LocalDateTime.now())
              .isRead(false)
              .build();
      createNotification(adminRequest);

      // Notify branch manager based on branchId
      Long managerId = getBranchManagerId(branchId);
      if (managerId != null && !managerId.equals(1L)) {
        NotificationRequest managerRequest =
            NotificationRequest.builder()
                .userId(managerId)
                .message(
                    String.format(
                        "📦 Yêu cầu trả hàng mới: Có yêu cầu trả hàng #%d cho chi nhánh của bạn. Vui lòng xem xét.",
                        returnOrderId))
                .notificationType("RETURN_ORDER_CREATED")
                .notificationDate(LocalDateTime.now())
                .isRead(false)
                .build();
        createNotification(managerRequest);
      }
    } catch (Exception e) {
      log.error(
          "Failed to send return order created notification for returnOrderId: {}",
          returnOrderId,
          e);
    }
  }

  /** Send notification when a return order is approved - Notifies customer */
  @Async("emailTaskExecutor")
  @Transactional
  public void sendReturnOrderApprovedNotification(
      Long returnOrderId, Long customerId, String reason) {
    try {
      String message =
          String.format(
              "✅ Yêu cầu trả hàng được chấp nhận: Yêu cầu trả hàng #%d của bạn đã được phê duyệt.",
              returnOrderId);

      if (reason != null && !reason.trim().isEmpty()) {
        message += " Ghi chú: " + reason;
      }
      message += " Vui lòng chuẩn bị hàng để gửi trả.";

      NotificationRequest request =
          NotificationRequest.builder()
              .userId(customerId)
              .message(message)
              .notificationType("RETURN_ORDER_APPROVED")
              .notificationDate(LocalDateTime.now())
              .isRead(false)
              .build();
      createNotification(request);
    } catch (Exception e) {
      log.error(
          "Failed to send return order approved notification for returnOrderId: {}",
          returnOrderId,
          e);
    }
  }

  /** Send notification when a return order is rejected - Notifies customer */
  @Async("emailTaskExecutor")
  @Transactional
  public void sendReturnOrderRejectedNotification(
      Long returnOrderId, Long customerId, String reason) {
    try {
      String message =
          String.format(
              "❌ Yêu cầu trả hàng bị từ chối: Yêu cầu trả hàng #%d của bạn đã bị từ chối.",
              returnOrderId);

      if (reason != null && !reason.trim().isEmpty()) {
        message += " Lý do: " + reason;
      }

      NotificationRequest request =
          NotificationRequest.builder()
              .userId(customerId)
              .message(message)
              .notificationType("RETURN_ORDER_REJECTED")
              .notificationDate(LocalDateTime.now())
              .isRead(false)
              .build();
      createNotification(request);
    } catch (Exception e) {
      log.error(
          "Failed to send return order rejected notification for returnOrderId: {}",
          returnOrderId,
          e);
    }
  }

  /** Send notification when return items are received and processed - Notifies customer */
  @Async("emailTaskExecutor")
  @Transactional
  public void sendReturnOrderCompletedNotification(
      Long returnOrderId, Long customerId, String refundInfo) {
    try {
      String message =
          String.format(
              "🎉 Trả hàng hoàn tất: Yêu cầu trả hàng #%d đã được xử lý thành công.",
              returnOrderId);

      if (refundInfo != null && !refundInfo.trim().isEmpty()) {
        message += " " + refundInfo;
      }

      NotificationRequest request =
          NotificationRequest.builder()
              .userId(customerId)
              .message(message)
              .notificationType("RETURN_ORDER_COMPLETED")
              .notificationDate(LocalDateTime.now())
              .isRead(false)
              .build();
      createNotification(request);
    } catch (Exception e) {
      log.error(
          "Failed to send return order completed notification for returnOrderId: {}",
          returnOrderId,
          e);
    }
  }

  /** Helper method to get branch manager ID based on branchId */
  private Long getBranchManagerId(Long branchId) {
    // Map branchId to manager userId
    // Branch 1 -> Manager 1 (userId might be 2), Branch 2 -> Manager 2 (userId might be 3), etc.
    // This mapping should be based on your actual user data
    return switch (branchId.intValue()) {
      case 1 -> 2L; // Manager of branch 1
      case 2 -> 3L; // Manager of branch 2
      case 3 -> 4L; // Manager of branch 3
      default -> null;
    };
  }

  /** Send notification for auto-created transfer requests Used by AutoTransferJob */
  @Async("emailTaskExecutor")
  @Transactional
  public void sendAutoTransferCreatedNotification(Transfer transfer) {
    try {
      // Notify admin (approver)
      if (transfer.getApprover() != null) {
        NotificationRequest adminRequest =
            NotificationRequest.builder()
                .userId(transfer.getApprover().getUserId())
                .message(
                    String.format(
                        "🤖 Yêu cầu chuyển kho tự động: Hệ thống đã tự động tạo yêu cầu chuyển kho #%d từ %s đến %s với %d sản phẩm do tồn kho thấp.",
                        transfer.getTransferRequestId(),
                        transfer.getFromInventory().getName(),
                        transfer.getToInventory().getName(),
                        transfer.getTransferItems().size()))
                .notificationType("AUTO_TRANSFER_CREATED")
                .notificationDate(LocalDateTime.now())
                .isRead(false)
                .build();
        createNotification(adminRequest);
      }

      // Notify manager (requester)
      if (transfer.getRequester() != null) {
        assert transfer.getApprover() != null;
        if (!transfer.getRequester().getUserId().equals(transfer.getApprover().getUserId())) {
          NotificationRequest managerRequest =
              NotificationRequest.builder()
                  .userId(transfer.getRequester().getUserId())
                  .message(
                      String.format(
                          "🤖 Yêu cầu chuyển kho tự động: Hệ thống đã tự động tạo yêu cầu chuyển kho #%d cho kho %s do tồn kho thấp. Yêu cầu đang chờ duyệt.",
                          transfer.getTransferRequestId(), transfer.getToInventory().getName()))
                  .notificationType("AUTO_TRANSFER_CREATED")
                  .notificationDate(LocalDateTime.now())
                  .isRead(false)
                  .build();
          createNotification(managerRequest);
        }
      }
    } catch (Exception ignored) {

    }
  }

  private void pushToUser(Notification notification) {
    if (notification.getUser() == null) {
      return;
    }
    String type = notification.getNotificationType();
    String title =
        switch (type == null ? "" : type) {
          case "order_created" -> "Đơn hàng mới";
          case "order_cancelled" -> "Đơn hàng bị huỷ";
          case "low_stock" -> "Cảnh báo tồn kho";
          case "product_new" -> "Sản phẩm mới";
          case "TRANSFER_PENDING" -> "Yêu cầu chuyển kho";
          case "TRANSFER_CREATED" -> "Yêu cầu chuyển kho";
          case "TRANSFER_APPROVED" -> "Chuyển kho được duyệt";
          case "TRANSFER_REJECTED" -> "Chuyển kho bị từ chối";
          case "AUTO_TRANSFER_CREATED" -> "Chuyển kho tự động";
          case "LOW_STOCK" -> "Cảnh báo tồn kho";
          case "RETURN_ORDER_CREATED" -> "Yêu cầu trả hàng";
          case "RETURN_ORDER_APPROVED" -> "Trả hàng được duyệt";
          case "RETURN_ORDER_REJECTED" -> "Trả hàng bị từ chối";
          case "RETURN_ORDER_COMPLETED" -> "Trả hàng hoàn tất";
          default -> "Thông báo";
        };
    Map<String, String> data =
        Map.of(
            "notificationId",
            String.valueOf(notification.getNotificationId()),
            "type",
            type == null ? "" : type);
    fcmService.pushNotification(
        notification.getUser().getUserId(), title, notification.getMessage(), data);
  }
}
