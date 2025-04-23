package com.market.MSA.repositories.order;

import com.market.MSA.models.order.CancelOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CancelOrderRepository extends JpaRepository<CancelOrder, Long> {}
