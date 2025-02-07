package com.market.MSA.services;

import com.market.MSA.exceptions.AppException;
import com.market.MSA.exceptions.ErrorCode;
import com.market.MSA.mappers.OrderDetailMapper;
import com.market.MSA.models.OrderDetail;
import com.market.MSA.repositories.OrderDetailRepository;
import com.market.MSA.requests.OrderDetailRequest;
import com.market.MSA.responses.OrderDetailResponse;
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
  private final OrderDetailRepository orderDetailRepository;
  private final OrderDetailMapper orderDetailMapper;

  // Tạo chi tiết đơn hàng
  @Transactional
  public OrderDetailResponse createOrderDetail(OrderDetailRequest request) {
    OrderDetail orderDetail = orderDetailMapper.toOrderDetail(request);
    OrderDetail savedOrderDetail = orderDetailRepository.save(orderDetail);
    return orderDetailMapper.toOrderDetailResponse(savedOrderDetail);
  }

  // Xóa chi tiết đơn hàng
  @Transactional
  public void deleteOrderDetail(Long id) {
    orderDetailRepository.deleteById(id);
  }

  // Cập nhật chi tiết đơn hàng
  @Transactional
  public OrderDetailResponse updateOrderDetail(Long id, OrderDetailRequest request) {
    OrderDetail orderDetail =
        orderDetailRepository
            .findById(id)
            .orElseThrow(() -> new AppException(ErrorCode.ORDER_DETAIL_NOT_FOUND));

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
