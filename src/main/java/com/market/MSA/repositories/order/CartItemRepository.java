package com.market.MSA.repositories.order;

import com.market.MSA.models.order.CartItem;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
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

  @Transactional
  @Modifying
  @Query("UPDATE CartItem c SET c.isSelected = :isSelected WHERE c.cart.cartId = :cartId")
  void updateCartItemsSelectionByCartId(
      @Param("cartId") Long cartId, @Param("isSelected") boolean isSelected);

  @Query(
      "SELECT c FROM CartItem c WHERE c.cart.cartId = :cartId AND c.product.productId = :productId")
  @EntityGraph(attributePaths = {"product", "cart"})
  Optional<CartItem> findByCart_CartIdAndProduct_ProductId(
      @Param("cartId") Long cartId, @Param("productId") Long productId);

  @Query(
      "SELECT c FROM CartItem c WHERE c.cart.cartId = :cartId AND c.product.productId = :productId AND c.isFreeItem = true")
  @EntityGraph(attributePaths = {"product", "cart"})
  Optional<CartItem> findByCart_CartIdAndProduct_ProductIdAndIsFreeItemTrue(
      @Param("cartId") Long cartId, @Param("productId") Long productId);

  @Query("SELECT c FROM CartItem c WHERE c.cart.cartId = :cartId AND c.isSelected = :isSelected")
  @EntityGraph(attributePaths = {"product", "cart"})
  List<CartItem> findByCart_CartIdAndIsSelected(
      @Param("cartId") Long cartId, @Param("isSelected") boolean isSelected);

  /**
   * Optimized query to fetch cart items along with product images and category in a single SQL
   * statement, preventing N+1 issues when accessing these lazy relationships later.
   */
  @Query(
      "SELECT DISTINCT c FROM CartItem c "
          + "JOIN FETCH c.product p "
          + "LEFT JOIN FETCH p.images imgs "
          + "LEFT JOIN FETCH p.category cat "
          + "JOIN FETCH c.cart cart "
          + "WHERE cart.cartId = :cartId AND c.isSelected = :isSelected")
  List<CartItem> findWithDetailsByCartIdAndIsSelected(
      @Param("cartId") Long cartId, @Param("isSelected") boolean isSelected);

  /** Fetch all cart items (selected and unselected) with full details to avoid N+1. */
  @Query(
      "SELECT DISTINCT c FROM CartItem c "
          + "JOIN FETCH c.product p "
          + "LEFT JOIN FETCH p.images imgs "
          + "LEFT JOIN FETCH p.category cat "
          + "JOIN FETCH c.cart cart "
          + "WHERE cart.cartId = :cartId")
  List<CartItem> findWithDetailsByCartId(@Param("cartId") Long cartId);

  List<CartItem> findByCart_CartId(@Param("cartId") Long cartId);

  @Query(
      "SELECT COALESCE(SUM(c.price * c.quantity), 0) FROM CartItem c WHERE c.cart.cartId = :cartId AND c.isSelected = true")
  Double calculateCartTotal(@Param("cartId") Long cartId);

  // Delete all free items in a cart – used to re-synchronise bundle items
  @Transactional
  @Modifying
  @Query("DELETE FROM CartItem c WHERE c.cart.cartId = :cartId AND c.isFreeItem = true")
  void deleteFreeItemsByCartId(@Param("cartId") Long cartId);
}
