package com.market.MSA.repositories;

import com.market.MSA.models.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
  // Tham chiếu đến thuộc tính user của Order
  Page<Order> findByUser_UserIdAndStatus(Long userId, String status, Pageable pageable);

  // Tham chiếu đến thuộc tính user của Order để tìm theo số điện thoại
  Page<Order> findByUser_PhoneNumber(String phoneNumber, Pageable pageable);
}
