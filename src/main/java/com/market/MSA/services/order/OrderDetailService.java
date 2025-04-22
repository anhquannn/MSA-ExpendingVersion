package com.market.MSA.services.order;

import com.market.MSA.exceptions.AppException;
import com.market.MSA.exceptions.ErrorCode;
import com.market.MSA.mappers.order.OrderDetailMapper;
import com.market.MSA.models.order.OrderDetail;
import com.market.MSA.repositories.order.OrderDetailRepository;
import com.market.MSA.repositories.order.OrderRepository;
import com.market.MSA.repositories.product.ProductRepository;
import com.market.MSA.requests.order.OrderDetailRequest;
import com.market.MSA.responses.order.OrderDetailResponse;
import com.market.MSA.services.others.EntityFinderService;
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
public class OrderDetailService {
  final EntityFinderService entityFinderService;

  final OrderDetailRepository orderDetailRepository;
  final OrderRepository orderRepository;
  final ProductRepository productRepository;

  final OrderDetailMapper orderDetailMapper;

  // Tạo chi tiết đơn hàng
  @Transactional
  public OrderDetailResponse createOrderDetail(OrderDetailRequest request) {
    OrderDetail orderDetail = orderDetailMapper.toOrderDetail(request);
    orderDetail.setOrder(
        entityFinderService.findByIdOrThrow(
            orderRepository, request.getOrderId(), ErrorCode.ORDER_NOT_FOUND));
    orderDetail.setProduct(
        entityFinderService.findByIdOrThrow(
            productRepository, request.getProductId(), ErrorCode.PRODUCT_NOT_FOUND));

    OrderDetail savedOrderDetail = orderDetailRepository.save(orderDetail);
    return orderDetailMapper.toOrderDetailResponse(savedOrderDetail);
  }

  // Xóa chi tiết đơn hàng
  @Transactional
  public boolean deleteOrderDetail(Long id) {
    if (!orderDetailRepository.existsById(id)) {
      throw new AppException(ErrorCode.ORDER_DETAIL_NOT_FOUND);
    }
    orderDetailRepository.deleteById(id);
    return true;
  }

  // Cập nhật chi tiết đơn hàng
  @Transactional
  public OrderDetailResponse updateOrderDetail(Long id, OrderDetailRequest request) {
    OrderDetail orderDetail =
        orderDetailRepository
            .findById(id)
            .orElseThrow(() -> new AppException(ErrorCode.ORDER_DETAIL_NOT_FOUND));
    orderDetail.setOrder(
        entityFinderService.findByIdOrThrow(
            orderRepository, request.getOrderId(), ErrorCode.ORDER_NOT_FOUND));
    orderDetail.setProduct(
        entityFinderService.findByIdOrThrow(
            productRepository, request.getProductId(), ErrorCode.PRODUCT_NOT_FOUND));

    orderDetailMapper.updateOrderDetailFromRequest(request, orderDetail);
    OrderDetail updatedOrderDetail = orderDetailRepository.save(orderDetail);
    return orderDetailMapper.toOrderDetailResponse(updatedOrderDetail);
  }

  // Lấy chi tiết đơn hàng theo ID
  public OrderDetailResponse getOrderDetailById(Long id) {
    OrderDetail orderDetail =
        orderDetailRepository
            .findById(id)
            .orElseThrow(() -> new AppException(ErrorCode.ORDER_DETAIL_NOT_FOUND));
    return orderDetailMapper.toOrderDetailResponse(orderDetail);
  }

  // Lấy danh sách chi tiết đơn hàng theo Order ID
  public List<OrderDetailResponse> getOrderDetailsByOrderId(Long orderId) {
    return orderDetailRepository.findAll().stream()
        .filter(od -> od.getOrder().getOrderId() == orderId)
        .map(orderDetailMapper::toOrderDetailResponse)
        .collect(Collectors.toList());
  }

  // Lấy danh sách chi tiết đơn hàng theo Order ID
  public List<OrderDetail> findOrderDetailsByOrderId(Long orderId) {
    return orderDetailRepository.findAll().stream()
        .filter(od -> od.getOrder().getOrderId() == orderId)
        .collect(Collectors.toList());
  }
}
