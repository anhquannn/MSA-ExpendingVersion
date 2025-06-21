package com.market.MSA.repositories.order;

import com.market.MSA.models.order.CartItem;
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
  @Query("DELETE FROM CartItem c WHERE c.cart.cartId = :cartId AND c.isSelected = true")
  void clearCart(@Param("cartId") Long cartId);

  @Transactional
  @Modifying
  @Query(
      "UPDATE CartItem c SET c.isSelected = :isSelected, c.quantity = :quantity WHERE c.cartItemId = :cartItemId")
  void updateCartItem(
      @Param("cartItemId") Long cartItemId,
      @Param("isSelected") boolean isSelected,
      @Param("quantity") int quantity);

  @Transactional
  @Modifying
  @Query("UPDATE CartItem c SET c.isSelected = :isSelected WHERE c.cartItemId IN :cartItemIds")
  void updateCartItemsSelection(
      @Param("cartItemIds") List<Long> cartItemIds, @Param("isSelected") boolean isSelected);

  @Query(
      "SELECT c FROM CartItem c WHERE c.cart.cartId = :cartId AND c.product.productId = :productId")
  Optional<CartItem> findByCart_CartIdAndProduct_ProductId(
      @Param("cartId") Long cartId, @Param("productId") Long productId);

  @Query("SELECT c FROM CartItem c WHERE c.cart.cartId = :cartId AND c.isSelected = :isSelected")
  List<CartItem> findByCart_CartIdAndIsSelected(
      @Param("cartId") Long cartId, @Param("isSelected") boolean isSelected);

  @Query("SELECT c FROM CartItem c WHERE c.cart.cartId = :cartId")
  List<CartItem> findByCart_CartId(@Param("cartId") Long cartId);

  @Query(
      "SELECT COALESCE(SUM(c.price * c.quantity), 0) FROM CartItem c WHERE c.cart.cartId = :cartId AND c.isSelected = true")
  Double calculateCartTotal(@Param("cartId") Long cartId);
}
