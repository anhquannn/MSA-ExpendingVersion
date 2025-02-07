package com.market.MSA.repositories;

import com.market.MSA.models.CartItem;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
  @Transactional
  @Modifying
  @Query("DELETE FROM CartItem c WHERE c.id = :cartItemId")
  void deleteCartItem(@Param("cartItemId") Long cartItemId);

  @Transactional
  @Modifying
  @Query("DELETE FROM CartItem c WHERE c.cart.id = :cartId AND c.status = :status")
  void clearCart(@Param("cartId") Long cartId, @Param("status") String status);

  @Transactional
  @Modifying
  @Query(
      "UPDATE CartItem c SET c.status = :status, c.quantity = :quantity WHERE c.id = :cartItemId")
  void updateCartItem(
      @Param("cartItemId") Long cartItemId,
      @Param("status") String status,
      @Param("quantity") int quantity);

  @Transactional
  @Modifying
  @Query("UPDATE CartItem c SET c.status = :status WHERE c.id IN :cartItemIds")
  void updateCartItemsStatus(
      @Param("cartItemIds") List<Long> cartItemIds, @Param("status") String status);

  @Query("SELECT c FROM CartItem c WHERE c.cart.id = :cartId AND c.product.id = :productId")
  Optional<CartItem> findByCart_CartIdAndProduct_ProductId(
      @Param("cartId") Long cartId, @Param("productId") Long productId);

  @Query("SELECT c FROM CartItem c WHERE c.cart.id = :cartId AND c.status = :status")
  List<CartItem> findByCart_CartIdAndStatus(
      @Param("cartId") Long cartId, @Param("status") String status);

  @Query("SELECT c FROM CartItem c WHERE c.cart.id = :cartId")
  List<CartItem> findByCart_CartId(@Param("cartId") Long cartId);

  @Query("SELECT c FROM CartItem c WHERE c.id = :id")
  Optional<CartItem> getCartItemByID(@Param("id") Long id);

  @Query(
      "SELECT SUM(c.price * c.quantity) FROM CartItem c WHERE c.cart.id = :cartId AND c.status = 'available'")
  Double calculateCartTotal(@Param("cartId") Long cartId);
}
