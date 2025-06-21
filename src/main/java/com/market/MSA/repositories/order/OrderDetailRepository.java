package com.market.MSA.repositories.order;

import com.market.MSA.models.order.OrderDetail;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {

  List<OrderDetail> findByOrder_OrderId(long orderId);
}
