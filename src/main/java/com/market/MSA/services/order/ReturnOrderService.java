package com.market.MSA.services.order;

import com.market.MSA.constants.OrderStatus;
import com.market.MSA.constants.ReturnStatus;
import com.market.MSA.exceptions.AppException;
import com.market.MSA.exceptions.ErrorCode;
import com.market.MSA.mappers.order.ReturnOrderMapper;
import com.market.MSA.models.order.Order;
import com.market.MSA.models.order.ReturnOrder;
import com.market.MSA.models.others.DeliveryInfo;
import com.market.MSA.models.user.User;
import com.market.MSA.repositories.order.OrderDetailRepository;
import com.market.MSA.repositories.order.OrderRepository;
import com.market.MSA.repositories.order.ReturnItemImageRepository;
import com.market.MSA.repositories.order.ReturnOrderItemRepository;
import com.market.MSA.repositories.order.ReturnOrderRepository;
import com.market.MSA.repositories.user.UserRepository;
import com.market.MSA.requests.filters.ReturnOrderFilterRequest;
import com.market.MSA.requests.order.ReturnOrderRequest;
import com.market.MSA.requests.order.ReturnOrderStatusUpdateRequest;
import com.market.MSA.responses.order.ReturnOrderResponse;
import com.market.MSA.services.others.GoshipService;
import com.market.MSA.services.others.NotificationService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReturnOrderService {

  ReturnOrderRepository returnOrderRepository;
  ReturnOrderItemRepository returnOrderItemRepository;
  ReturnItemImageRepository returnItemImageRepository;
  OrderRepository orderRepository;
  OrderDetailRepository orderDetailRepository;
  UserRepository userRepository;

  ReturnOrderMapper returnOrderMapper;
  NotificationService notificationService;
  GoshipService goshipService;

  /** Tạo yêu cầu trả hàng mới */
  @Transactional
  @CacheEvict(
      value = {"return_orders", "return_orders_paging"},
      allEntries = true)
  public ReturnOrderResponse createReturnOrder(ReturnOrderRequest request) {
    log.info(
        "Creating return order for orderId: {}, userId: {}",
        request.getOrderId(),
        request.getUserId());

    // 1. Validate order và user
    Order order =
        orderRepository
            .findById(request.getOrderId())
            .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

    User user =
        userRepository
            .findById(request.getUserId())
            .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

    // 2. Validate order status - chỉ cho phép return khi order đã COMPLETED
    if (order.getStatus() != OrderStatus.COMPLETED) {
      throw new AppException(ErrorCode.INVALID_ORDER_STATUS);
    }

    // 3. Validate ownership - user phải là người đã đặt order
    if (!order.getUser().getUserId().equals(user.getUserId())) {
      throw new AppException(ErrorCode.UNAUTHORIZED_ACCESS);
    }

    // 4. Validate return items
    validateReturnItems(request, order);

    // 5. Tạo return order
    ReturnOrder returnOrder = returnOrderMapper.toReturnOrder(request);
    returnOrder.setOrder(order);
    returnOrder.setUser(user);
    returnOrder.setStatus(ReturnStatus.PENDING);
    returnOrder.setCreatedAt(LocalDateTime.now());
    returnOrder.setUpdatedAt(LocalDateTime.now());

    // 6. Tạo return order items và images
    createReturnOrderItems(returnOrder, request);

    // 7. Tính refund amount
    BigDecimal refundAmount = calculateRefundAmount(returnOrder);
    returnOrder.setRefundAmount(refundAmount);

    // 8. Save return order
    returnOrder = returnOrderRepository.save(returnOrder);

    // 9. Gửi notification
    sendReturnOrderCreatedNotification(returnOrder);

    // Lưu ý: Không tạo shipment ngay, chỉ tạo khi approve
    log.info(
        "Created return order with ID: {} - Waiting for approval", returnOrder.getReturnOrderId());
    return returnOrderMapper.toReturnOrderResponse(returnOrder);
  }

  // Helper methods sẽ được thêm vào sau
  private void validateReturnItems(ReturnOrderRequest request, Order order) {
    // TODO: Implement validation logic
  }

  private void createReturnOrderItems(ReturnOrder returnOrder, ReturnOrderRequest request) {
    // TODO: Implement creation logic
  }

  private BigDecimal calculateRefundAmount(ReturnOrder returnOrder) {
    // TODO: Implement calculation logic
    return BigDecimal.ZERO;
  }

  private void sendReturnOrderCreatedNotification(ReturnOrder returnOrder) {
    try {
      notificationService.sendReturnOrderCreatedNotification(
          returnOrder.getReturnOrderId(), returnOrder.getOrder().getBranch().getBranchId());
    } catch (Exception e) {
      log.error("Failed to send return order created notification", e);
    }
  }

  /**
   * Tạo shipment cho return order sau khi đã approve Shipment sẽ được tạo từ địa chỉ trong
   * DeliveryInfo của order gốc đến branch Chỉ gọi method này khi return order đã được approve
   */
  private void createReturnShipment(ReturnOrder returnOrder) {
    try {
      log.info("Creating return shipment for return order: {}", returnOrder.getReturnOrderId());

      // Lấy thông tin giao hàng của order gốc
      DeliveryInfo deliveryInfo = returnOrder.getOrder().getDeliveryInfo();
      if (deliveryInfo == null) {
        log.error("No delivery info found for order: {}", returnOrder.getOrder().getOrderId());
        return;
      }

      Long branchId = returnOrder.getOrder().getBranch().getBranchId();

      // Tạo rates trước (có thể sử dụng giá trị mặc định cho return)
      String defaultRate = "STANDARD"; // Hoặc lấy từ config

      // Tạo shipment cho return order sử dụng method mới
      var shipmentResponse =
          goshipService.createReturnShipment(
              returnOrder.getReturnOrderId(), deliveryInfo, branchId, defaultRate);

      // Cập nhật shipping code vào return order
      if (shipmentResponse != null && shipmentResponse.getId() != null) {
        returnOrder.setShippingCode(String.valueOf(shipmentResponse.getId()));
        returnOrder.setShippingStatus("CREATED");
        returnOrderRepository.save(returnOrder);

        log.info(
            "Created return shipment with ID: {} for return order: {}",
            shipmentResponse.getId(),
            returnOrder.getReturnOrderId());
      }

    } catch (Exception e) {
      log.error(
          "Failed to create return shipment for return order: {}",
          returnOrder.getReturnOrderId(),
          e);
      // Không throw exception để không làm fail toàn bộ quá trình tạo return order
    }
  }

  /** Cập nhật trạng thái return order */
  @Transactional
  @CacheEvict(
      value = {"return_orders", "return_orders_paging"},
      allEntries = true)
  public ReturnOrderResponse updateReturnOrderStatus(
      Long returnOrderId, ReturnOrderStatusUpdateRequest request) {
    log.info("Updating return order status: {} to {}", returnOrderId, request.getStatus());

    ReturnOrder returnOrder =
        returnOrderRepository
            .findById(returnOrderId)
            .orElseThrow(() -> new AppException(ErrorCode.RETURN_ORDER_NOT_FOUND));

    returnOrder.setStatus(request.getStatus());
    returnOrder.setReason(request.getReason());
    returnOrder.setShippingCode(request.getShippingCode());
    returnOrder.setShippingStatus(request.getShippingStatus());
    returnOrder.setUpdatedAt(LocalDateTime.now());

    returnOrder = returnOrderRepository.save(returnOrder);
    return returnOrderMapper.toReturnOrderResponse(returnOrder);
  }

  /** Lấy thông tin return order theo ID */
  @Transactional(readOnly = true)
  public ReturnOrderResponse getReturnOrderById(Long returnOrderId) {
    ReturnOrder returnOrder =
        returnOrderRepository
            .findById(returnOrderId)
            .orElseThrow(() -> new AppException(ErrorCode.RETURN_ORDER_NOT_FOUND));
    return returnOrderMapper.toReturnOrderResponse(returnOrder);
  }

  /** Lấy danh sách return orders theo user */
  @Transactional(readOnly = true)
  public List<ReturnOrderResponse> getReturnOrdersByUser(Long userId) {
    List<ReturnOrder> returnOrders = returnOrderRepository.findByUserUserId(userId);
    return returnOrders.stream()
        .map(returnOrderMapper::toReturnOrderResponse)
        .collect(java.util.stream.Collectors.toList());
  }

  /** Lấy danh sách return orders theo branch */
  @Transactional(readOnly = true)
  public List<ReturnOrderResponse> getReturnOrdersByBranch(Long branchId, String status) {
    List<ReturnOrder> returnOrders;
    if (status != null) {
      ReturnStatus returnStatus = ReturnStatus.valueOf(status.toUpperCase());
      returnOrders = returnOrderRepository.findByBranchIdAndStatus(branchId, returnStatus);
    } else {
      returnOrders = returnOrderRepository.findByBranchId(branchId);
    }
    return returnOrders.stream()
        .map(returnOrderMapper::toReturnOrderResponse)
        .collect(java.util.stream.Collectors.toList());
  }

  /** Phê duyệt return order và tạo inbound transfer */
  @Transactional
  @CacheEvict(
      value = {"return_orders", "return_orders_paging"},
      allEntries = true)
  public ReturnOrderResponse approveReturnOrder(Long returnOrderId, String reason) {
    log.info("Approving return order: {}", returnOrderId);

    ReturnOrder returnOrder =
        returnOrderRepository
            .findById(returnOrderId)
            .orElseThrow(() -> new AppException(ErrorCode.RETURN_ORDER_NOT_FOUND));

    returnOrder.setStatus(ReturnStatus.APPROVED);
    returnOrder.setReason(reason);
    returnOrder.setUpdatedAt(LocalDateTime.now());

    // Tạo shipment cho việc trả hàng khi đã approve
    createReturnShipment(returnOrder);

    // TODO: Tạo inbound transfer để nhập lại hàng vào kho
    // createInboundTransferForReturn(returnOrder);

    returnOrder = returnOrderRepository.save(returnOrder);

    // Gửi notification cho customer
    try {
      notificationService.sendReturnOrderApprovedNotification(
          returnOrder.getReturnOrderId(), returnOrder.getUser().getUserId(), reason);
    } catch (Exception e) {
      log.error("Failed to send return order approved notification", e);
    }

    return returnOrderMapper.toReturnOrderResponse(returnOrder);
  }

  /** Từ chối return order */
  @Transactional
  @CacheEvict(
      value = {"return_orders", "return_orders_paging"},
      allEntries = true)
  public ReturnOrderResponse rejectReturnOrder(Long returnOrderId, String reason) {
    log.info("Rejecting return order: {}", returnOrderId);

    ReturnOrder returnOrder =
        returnOrderRepository
            .findById(returnOrderId)
            .orElseThrow(() -> new AppException(ErrorCode.RETURN_ORDER_NOT_FOUND));

    returnOrder.setStatus(ReturnStatus.REJECTED);
    returnOrder.setReason(reason);
    returnOrder.setUpdatedAt(LocalDateTime.now());

    returnOrder = returnOrderRepository.save(returnOrder);

    // Gửi notification cho customer
    try {
      notificationService.sendReturnOrderRejectedNotification(
          returnOrder.getReturnOrderId(), returnOrder.getUser().getUserId(), reason);
    } catch (Exception e) {
      log.error("Failed to send return order rejected notification", e);
    }

    return returnOrderMapper.toReturnOrderResponse(returnOrder);
  }

  /** Lọc return orders với phân trang */
  @Transactional(readOnly = true)
  @Cacheable(value = "return_orders_paging", key = "#request")
  public Page<ReturnOrderResponse> filterReturnOrdersWithPaging(ReturnOrderFilterRequest request) {
    log.info("Filtering return orders with paging: {}", request);

    // Tạo Pageable object
    Sort sort =
        Sort.by(
            request.getSortDirection().equalsIgnoreCase("ASC")
                ? Sort.Direction.ASC
                : Sort.Direction.DESC,
            request.getSortBy());

    Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);

    // Gọi repository method
    Page<ReturnOrder> returnOrders =
        returnOrderRepository.filterWithPaging(
            request.getStatus(),
            request.getUserId(),
            request.getOrderId(),
            request.getBranchId(),
            request.getFromDate(),
            request.getToDate(),
            pageable);

    // Convert to response
    return returnOrders.map(returnOrderMapper::toReturnOrderResponse);
  }

  /** Lọc return orders không phân trang */
  @Transactional(readOnly = true)
  @Cacheable(value = "return_orders", key = "#request")
  public List<ReturnOrderResponse> filterReturnOrders(ReturnOrderFilterRequest request) {
    log.info("Filtering return orders: {}", request);

    // Gọi repository method
    List<ReturnOrder> returnOrders =
        returnOrderRepository.filter(
            request.getStatus(),
            request.getUserId(),
            request.getOrderId(),
            request.getBranchId(),
            request.getFromDate(),
            request.getToDate());

    // Convert to response
    return returnOrders.stream()
        .map(returnOrderMapper::toReturnOrderResponse)
        .collect(java.util.stream.Collectors.toList());
  }
}
