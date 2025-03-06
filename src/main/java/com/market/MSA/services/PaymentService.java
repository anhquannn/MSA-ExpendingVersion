package com.market.MSA.services;

import com.market.MSA.exceptions.AppException;
import com.market.MSA.exceptions.ErrorCode;
import com.market.MSA.mappers.PaymentMapper;
import com.market.MSA.models.Payment;
import com.market.MSA.repositories.OrderRepository;
import com.market.MSA.repositories.PaymentRepository;
import com.market.MSA.repositories.UserRepository;
import com.market.MSA.requests.PaymentRequest;
import com.market.MSA.responses.PaymentResponse;
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
  public void deletePayment(Long id) {
    if (!paymentRepository.existsById(id)) {
      throw new AppException(ErrorCode.PAYMENT_NOT_FOUND);
    }
    paymentRepository.deleteById(id);
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
