package com.market.MSA.repositories.others;

import com.market.MSA.models.others.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {}
