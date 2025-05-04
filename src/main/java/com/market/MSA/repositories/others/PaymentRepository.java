package com.market.MSA.repositories.others;

import com.market.MSA.models.others.Payment;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
  Optional<Payment> findByTransactionId(String transactionId);
}
