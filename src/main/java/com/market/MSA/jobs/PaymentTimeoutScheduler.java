package com.market.MSA.jobs;

import com.market.MSA.constants.OrderStatus;
import com.market.MSA.models.order.Order;
import com.market.MSA.models.others.Payment;
import com.market.MSA.repositories.order.OrderRepository;
import com.market.MSA.repositories.others.PaymentRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Scheduler to cancel VNPay payments that were not completed within the allowed window (15
 * minutes). Runs every 5 minutes.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentTimeoutScheduler {

  private final PaymentRepository paymentRepository;
  private final OrderRepository orderRepository;

  @Scheduled(fixedRate = 300_000) // every 5 minutes
  @Transactional
  public void cancelExpiredVNPayPayments() {
    LocalDateTime now = LocalDateTime.now();
    List<Payment> expired =
        paymentRepository.findAllByStatusAndPaymentMethodAndExpiryAtBefore(
            OrderStatus.PAYING, "vnpay", now);

    if (expired.isEmpty()) {
      return;
    }

    for (Payment payment : expired) {
      payment.setStatus(OrderStatus.FAILED);
      payment.setUpdateDate(now);

      Order order = payment.getOrder();
      if (order != null && !OrderStatus.CANCELLED.equals(order.getStatus())) {
        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
      }
      paymentRepository.save(payment);
      log.info(
          "Cancelled unpaid VNPay transaction {} for order {}",
          payment.getTransactionId(),
          order == null ? null : order.getOrderId());
    }
  }
}
