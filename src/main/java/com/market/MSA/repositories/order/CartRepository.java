package com.market.MSA.repositories.order;

import com.market.MSA.models.order.Cart;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {
  @EntityGraph(attributePaths = {"cartItems", "cartItems.product"})
  Optional<Cart> findByUser_UserId(Long userId);
}
