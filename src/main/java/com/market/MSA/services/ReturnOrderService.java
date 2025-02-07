package com.market.MSA.services;

import com.market.MSA.exceptions.AppException;
import com.market.MSA.exceptions.ErrorCode;
import com.market.MSA.mappers.ReturnOrderMapper;
import com.market.MSA.models.Order;
import com.market.MSA.models.OrderDetail;
import com.market.MSA.models.ReturnOrder;
import com.market.MSA.repositories.OrderDetailRepository;
import com.market.MSA.repositories.OrderRepository;
import com.market.MSA.repositories.ReturnOrderRepository;
import com.market.MSA.requests.ReturnOrderRequest;
import com.market.MSA.responses.ReturnOrderResponse;
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
public class ReturnOrderService {
  ReturnOrderRepository returnOrderRepository;
  OrderRepository orderRepository;
  OrderDetailRepository orderDetailRepository;
  ProductService productService;
  ReturnOrderMapper returnOrderMapper;

  @Transactional
  public ReturnOrderResponse createReturnOrder(ReturnOrderRequest request) {
    Order order =
        orderRepository
            .findById(request.getOrderId())
            .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

    // Cập nhật trạng thái đơn hàng gốc
    order.setStatus("returned");
    orderRepository.save(order);

    // Lưu đơn trả hàng vào cơ sở dữ liệu
    ReturnOrder returnOrder = returnOrderMapper.toReturnOrder(request);
    returnOrder.setOrder(order);
    returnOrder.setStatus("success");
    returnOrder = returnOrderRepository.save(returnOrder);

    // Lấy chi tiết sản phẩm trong đơn hàng gốc
    List<OrderDetail> orderDetails = orderDetailRepository.findByOrder_OrderId(order.getOrderId());

    // Khôi phục số lượng sản phẩm trong kho
    for (OrderDetail orderDetail : orderDetails) {
      productService.restoreStock(
          orderDetail.getProduct().getProductId(), orderDetail.getQuantity());
    }

    return returnOrderMapper.toReturnOrderResponse(returnOrder);
  }

  @Transactional
  public ReturnOrderResponse updateReturnOrder(Long id, ReturnOrderRequest request) {
    ReturnOrder returnOrder =
        returnOrderRepository
            .findById(id)
            .orElseThrow(() -> new AppException(ErrorCode.RETURN_ORDER_NOT_FOUND));

    returnOrderMapper.updateReturnOrderFromRequest(request, returnOrder);
    return returnOrderMapper.toReturnOrderResponse(returnOrderRepository.save(returnOrder));
  }

  @Transactional
  public void deleteReturnOrder(Long id) {
    if (!returnOrderRepository.existsById(id)) {
      throw new AppException(ErrorCode.RETURN_ORDER_NOT_FOUND);
    }
    returnOrderRepository.deleteById(id);
  }

  @Transactional(readOnly = true)
  public ReturnOrderResponse getReturnOrderById(Long id) {
    ReturnOrder returnOrder =
        returnOrderRepository
            .findById(id)
            .orElseThrow(() -> new AppException(ErrorCode.RETURN_ORDER_NOT_FOUND));
    return returnOrderMapper.toReturnOrderResponse(returnOrder);
  }

  @Transactional(readOnly = true)
  public List<ReturnOrderResponse> getAllReturnOrders() {
    return returnOrderRepository.findAll().stream()
        .map(returnOrderMapper::toReturnOrderResponse)
        .collect(Collectors.toList());
  }
}
