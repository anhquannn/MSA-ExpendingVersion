package com.market.MSA.repositories;

import com.market.MSA.models.Cart;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {
  Optional<Cart> findByUser_UserId(Long userId);
}
