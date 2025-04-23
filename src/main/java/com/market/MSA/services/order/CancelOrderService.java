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
import com.market.MSA.requests.order.CancelOrderRequest;
import com.market.MSA.responses.order.CancelOrderResponse;
import com.market.MSA.services.others.NotificationService;
import com.market.MSA.services.product.InventoryProductService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
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
  public CancelOrderResponse createCancelOrder(CancelOrderRequest request) {
    Order order =
        orderRepository
            .findById(request.getOrderId())
            .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

    // Cập nhật trạng thái đơn hàng gốc
    order.setStatus(OrderStatus.ORDER_STATUS_7.getStatus());
    orderRepository.save(order);

    // Lưu đơn trả hàng vào cơ sở dữ liệu
    CancelOrder cancelOrder =
        CancelOrder.builder()
            .cancelDate(request.getCancelDate())
            .reason(request.getReason())
            .order(order)
            .refundAmount(order.getGrandTotal())
            .status(OrderStatus.ORDER_STATUS_8.getStatus())
            .build();

    cancelOrder = cancelOrderRepository.save(cancelOrder);

    // Lấy chi tiết sản phẩm trong đơn hàng gốc
    List<OrderDetail> orderDetails = orderDetailRepository.findByOrder_OrderId(order.getOrderId());

    // Khôi phục số lượng sản phẩm trong kho
    for (OrderDetail orderDetail : orderDetails) {
      inventoryProductService.restoreStock(orderDetail.getOrder());
    }

    // Send notification
    notificationService.sendOrderCancelledNotification(order.getOrderId());

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

  @Transactional(readOnly = true)
  public List<CancelOrderResponse> getAllCancelOrders() {
    return cancelOrderRepository.findAll().stream()
        .map(cancelOrderMapper::toCancelOrderResponse)
        .collect(Collectors.toList());
  }
}
