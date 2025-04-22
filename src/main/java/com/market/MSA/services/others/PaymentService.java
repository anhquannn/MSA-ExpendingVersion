package com.market.MSA.services.others;

import com.market.MSA.exceptions.AppException;
import com.market.MSA.exceptions.ErrorCode;
import com.market.MSA.mappers.others.PaymentMapper;
import com.market.MSA.models.others.Payment;
import com.market.MSA.repositories.order.OrderRepository;
import com.market.MSA.repositories.others.PaymentRepository;
import com.market.MSA.repositories.user.UserRepository;
import com.market.MSA.requests.others.PaymentRequest;
import com.market.MSA.responses.others.PaymentResponse;
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
public class PaymentService {
  final EntityFinderService entityFinderService;
  final PaymentRepository paymentRepository;
  final UserRepository userRepository;
  final OrderRepository orderRepository;
  final PaymentMapper paymentMapper;

  @Transactional
  public PaymentResponse createPayment(PaymentRequest request) {
    Payment payment = paymentMapper.toPayment(request);
    payment.setUser(
        entityFinderService.findByIdOrThrow(
            userRepository, request.getUserId(), ErrorCode.USER_NOT_EXISTED));
    payment.setOrder(
        entityFinderService.findByIdOrThrow(
            orderRepository, request.getOrderId(), ErrorCode.ORDER_NOT_FOUND));

    return paymentMapper.toPaymentResponse(paymentRepository.save(payment));
  }

  @Transactional
  public PaymentResponse updatePayment(Long id, PaymentRequest request) {
    Payment payment =
        paymentRepository
            .findById(id)
            .orElseThrow(() -> new AppException(ErrorCode.PAYMENT_NOT_FOUND));
    payment.setUser(
        entityFinderService.findByIdOrThrow(
            userRepository, request.getUserId(), ErrorCode.USER_NOT_EXISTED));
    payment.setOrder(
        entityFinderService.findByIdOrThrow(
            orderRepository, request.getOrderId(), ErrorCode.ORDER_NOT_FOUND));

    paymentMapper.updatePaymentFromRequest(request, payment);
    return paymentMapper.toPaymentResponse(paymentRepository.save(payment));
  }

  @Transactional
  public boolean deletePayment(Long id) {
    if (!paymentRepository.existsById(id)) {
      throw new AppException(ErrorCode.PAYMENT_NOT_FOUND);
    }
    paymentRepository.deleteById(id);
    return true;
  }

  @Transactional(readOnly = true)
  public PaymentResponse getPaymentById(Long id) {
    Payment payment =
        paymentRepository
            .findById(id)
            .orElseThrow(() -> new AppException(ErrorCode.PAYMENT_NOT_FOUND));
    return paymentMapper.toPaymentResponse(payment);
  }

  @Transactional(readOnly = true)
  public List<PaymentResponse> getAllPayments() {
    return paymentRepository.findAll().stream()
        .map(paymentMapper::toPaymentResponse)
        .collect(Collectors.toList());
  }
}
