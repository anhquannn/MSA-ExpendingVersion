package com.market.MSA.services.order;

import com.market.MSA.constants.OrderStatus;
import com.market.MSA.exceptions.AppException;
import com.market.MSA.exceptions.ErrorCode;
import com.market.MSA.mappers.order.CancelOrderMapper;
import com.market.MSA.models.order.CancelOrder;
import com.market.MSA.models.order.Order;
import com.market.MSA.models.order.OrderDetail;
import com.market.MSA.repositories.order.CancelOrderRepository;
import com.market.MSA.repositories.order.OrderDetailRepository;
import com.market.MSA.repositories.order.OrderRepository;
import com.market.MSA.requests.filters.CancelOrderFilterRequest;
import com.market.MSA.requests.order.CancelOrderRequest;
import com.market.MSA.responses.order.CancelOrderResponse;
import com.market.MSA.services.others.NotificationService;
import com.market.MSA.services.product.InventoryProductService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
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
public class CancelOrderService {
  final CancelOrderRepository cancelOrderRepository;
  final OrderRepository orderRepository;
  final OrderDetailRepository orderDetailRepository;
  final CancelOrderMapper cancelOrderMapper;
  final InventoryProductService inventoryProductService;
  final NotificationService notificationService;

  @Transactional
  @CacheEvict(
      value = {"all_campaigns", "campaigns_list", "campaigns_paging"},
      allEntries = true)
  public CancelOrderResponse createCancelOrder(CancelOrderRequest request) {
    // 1. Tìm kiếm đơn hàng gốc trong CSDL bằng 'orderId' từ request.
    // Nếu không tìm thấy, ném ra một ngoại lệ 'AppException' với mã lỗi 'ORDER_NOT_FOUND'.
    Order order =
        orderRepository
            .findById(request.getOrderId())
            .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

    // 2. Cập nhật trạng thái của đơn hàng gốc thành 'CANCELLED' (Đã hủy).
    order.setStatus(OrderStatus.CANCELLED);
    // Lưu lại thay đổi vào CSDL.
    orderRepository.save(order);

    // 3. Tạo một đối tượng 'CancelOrder' mới để ghi lại thông tin về việc hủy đơn hàng.
    // Builder pattern được sử dụng để tạo đối tượng một cách linh hoạt.
    CancelOrder cancelOrder =
        CancelOrder.builder()
            .cancelDate(LocalDateTime.now()) // Ngày hủy là thời điểm hiện tại.
            .reason(request.getReason()) // Lý do hủy lấy từ request.
            .order(order) // Liên kết với đơn hàng gốc.
            .refundAmount(order.getGrandTotal()) // Số tiền hoàn lại bằng tổng tiền của đơn hàng.
            .status(OrderStatus.CANCELLED) // Trạng thái của việc hủy đơn.
            .build();

    // Lưu đối tượng 'cancelOrder' này vào CSDL.
    cancelOrder = cancelOrderRepository.save(cancelOrder);

    // 4. Lấy danh sách tất cả các chi tiết đơn hàng (các sản phẩm) từ đơn hàng gốc.
    List<OrderDetail> orderDetails = orderDetailRepository.findByOrder_OrderId(order.getOrderId());

    // 5. Duyệt qua từng sản phẩm trong đơn hàng đã hủy để xử lý.
    for (OrderDetail orderDetail : orderDetails) {
      // Gọi service 'inventoryProductService' để khôi phục (cộng lại) số lượng tồn kho cho sản
      // phẩm.
      inventoryProductService.restoreStock(orderDetail.getOrder());
      // Cập nhật trạng thái của từng mục sản phẩm trong đơn hàng thành 'CANCELLED'.
      orderDetail.setStatus(OrderStatus.CANCELLED);
    }

    // 6. Gọi 'notificationService' để gửi thông báo (ví dụ: email, push notification)
    // cho người dùng về việc đơn hàng của họ đã bị hủy.
    notificationService.sendOrderCancelledNotification(order.getOrderId());

    // 7. Sử dụng 'cancelOrderMapper' để chuyển đổi đối tượng 'CancelOrder' (entity)
    // thành 'CancelOrderResponse' (DTO - Data Transfer Object) và trả về cho client.
    return cancelOrderMapper.toCancelOrderResponse(cancelOrder);
  }

  @Transactional
  public CancelOrderResponse updateCancelOrder(Long id, CancelOrderRequest request) {
    CancelOrder cancelOrder =
        cancelOrderRepository
            .findById(id)
            .orElseThrow(() -> new AppException(ErrorCode.CANCEL_ORDER_NOT_FOUND));

    cancelOrderMapper.updateCancelOrderFromRequest(request, cancelOrder);
    return cancelOrderMapper.toCancelOrderResponse(cancelOrderRepository.save(cancelOrder));
  }

  @Transactional
  public boolean deleteCancelOrder(Long id) {
    if (!cancelOrderRepository.existsById(id)) {
      throw new AppException(ErrorCode.CANCEL_ORDER_NOT_FOUND);
    }
    cancelOrderRepository.deleteById(id);
    return true;
  }

  @Transactional(readOnly = true)
  public CancelOrderResponse getCancelOrderById(Long id) {
    CancelOrder cancelOrder =
        cancelOrderRepository
            .findById(id)
            .orElseThrow(() -> new AppException(ErrorCode.CANCEL_ORDER_NOT_FOUND));
    return cancelOrderMapper.toCancelOrderResponse(cancelOrder);
  }

  @Cacheable("all_cancel_orders")
  public List<CancelOrderResponse> getAll() {
    return cancelOrderRepository.findAll().stream()
        .map(cancelOrderMapper::toCancelOrderResponse)
        .collect(Collectors.toList());
  }

  @Cacheable("cancel_orders_list")
  public List<CancelOrderResponse> getAllCancelOrders(CancelOrderFilterRequest request) {
    Sort sort = Sort.by(Sort.Direction.fromString(request.getSortDirection()), request.getSortBy());
    return cancelOrderRepository
        .filter(
            request.getOrderId(),
            request.getUserId(),
            request.getStatus(),
            request.getReason(),
            request.getFromDate(),
            request.getToDate(),
            sort)
        .stream()
        .map(cancelOrderMapper::toCancelOrderResponse)
        .collect(Collectors.toList());
  }

  @Cacheable("cancel_orders_paging")
  public Page<CancelOrderResponse> getAllCancelOrdersWithPaging(CancelOrderFilterRequest request) {
    Sort sort = Sort.by(Sort.Direction.fromString(request.getSortDirection()), request.getSortBy());
    Pageable pageable = PageRequest.of(request.getPage() - 1, request.getPageSize(), sort);

    return cancelOrderRepository
        .filterWithPaging(
            request.getOrderId(),
            request.getUserId(),
            request.getStatus(),
            request.getReason(),
            request.getFromDate(),
            request.getToDate(),
            pageable)
        .map(cancelOrderMapper::toCancelOrderResponse);
  }
}
