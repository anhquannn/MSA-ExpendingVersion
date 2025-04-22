package com.market.MSA.repositories.others;

import com.market.MSA.models.others.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

  @Query(
      "SELECT n FROM Notification n WHERE n.user.userId = :userId "
          + "AND (:type IS NULL OR n.notificationType = :type) "
          + "AND (:isRead IS NULL OR n.isRead = :isRead) "
          + "AND (:productId IS NULL OR n.product.productId = :productId) "
          + "AND (:orderId IS NULL OR n.order.orderId = :orderId) "
          + "AND (:inventoryId IS NULL OR n.inventory.inventoryId = :inventoryId)")
  Page<Notification> findAllByUserIdWithFilters(
      @Param("userId") Long userId,
      @Param("type") String type,
      @Param("isRead") Boolean isRead,
      @Param("productId") Long productId,
      @Param("orderId") Long orderId,
      @Param("inventoryId") Long inventoryId,
      Pageable pageable);

  @Query(
      "SELECT n FROM Notification n WHERE "
          + "(:type IS NULL OR n.notificationType = :type) "
          + "AND (:isRead IS NULL OR n.isRead = :isRead)")
  Page<Notification> findAllWithFilters(
      @Param("type") String type, @Param("isRead") Boolean isRead, Pageable pageable);
}
