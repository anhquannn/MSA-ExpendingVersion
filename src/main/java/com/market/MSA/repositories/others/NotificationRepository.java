package com.market.MSA.repositories.others;

import com.market.MSA.models.others.Notification;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

  // --- Có phân trang: Lọc theo userId và các điều kiện ---
  @Query(
      "SELECT n FROM Notification n WHERE n.user.userId = :userId "
          + "AND (:type IS NULL OR n.notificationType = :type) "
          + "AND (:isRead IS NULL OR n.isRead = :isRead) "
          + "AND (:productId IS NULL OR n.product.productId = :productId) "
          + "AND (:orderId IS NULL OR n.order.orderId = :orderId) "
          + "AND (:inventoryId IS NULL OR n.inventory.inventoryId = :inventoryId) "
          + "AND (:fromDate IS NULL OR n.notificationDate >= :fromDate) "
          + "AND (:toDate IS NULL OR n.notificationDate <= :toDate)")
  Page<Notification> filterWithPaging(
      @Param("userId") Long userId,
      @Param("type") String type,
      @Param("isRead") Boolean isRead,
      @Param("productId") Long productId,
      @Param("orderId") Long orderId,
      @Param("inventoryId") Long inventoryId,
      @Param("fromDate") LocalDateTime fromDate,
      @Param("toDate") LocalDateTime toDate,
      Pageable pageable);

  // --- Có phân trang: Không cần userId ---
  @Query(
      "SELECT n FROM Notification n WHERE "
          + "(:type IS NULL OR n.notificationType = :type) "
          + "AND (:isRead IS NULL OR n.isRead = :isRead) "
          + "AND (:fromDate IS NULL OR n.notificationDate >= :fromDate) "
          + "AND (:toDate IS NULL OR n.notificationDate <= :toDate)")
  Page<Notification> filter(
      @Param("type") String type,
      @Param("isRead") Boolean isRead,
      @Param("fromDate") LocalDateTime fromDate,
      @Param("toDate") LocalDateTime toDate,
      Pageable pageable);

  // --- Không phân trang: Lọc theo userId và các điều kiện ---
  @Query(
      "SELECT n FROM Notification n WHERE n.user.userId = :userId "
          + "AND (:type IS NULL OR n.notificationType = :type) "
          + "AND (:isRead IS NULL OR n.isRead = :isRead) "
          + "AND (:productId IS NULL OR n.product.productId = :productId) "
          + "AND (:orderId IS NULL OR n.order.orderId = :orderId) "
          + "AND (:inventoryId IS NULL OR n.inventory.inventoryId = :inventoryId) "
          + "AND (:fromDate IS NULL OR n.notificationDate >= :fromDate) "
          + "AND (:toDate IS NULL OR n.notificationDate <= :toDate)")
  List<Notification> findAllByUserIdWithFiltersNoPaging(
      @Param("userId") Long userId,
      @Param("type") String type,
      @Param("isRead") Boolean isRead,
      @Param("productId") Long productId,
      @Param("orderId") Long orderId,
      @Param("inventoryId") Long inventoryId,
      @Param("fromDate") LocalDateTime fromDate,
      @Param("toDate") LocalDateTime toDate,
      Sort sort);

  // --- Không phân trang: Không cần userId ---
  @Query(
      "SELECT n FROM Notification n WHERE "
          + "(:type IS NULL OR n.notificationType = :type) "
          + "AND (:isRead IS NULL OR n.isRead = :isRead) "
          + "AND (:fromDate IS NULL OR n.notificationDate >= :fromDate) "
          + "AND (:toDate IS NULL OR n.notificationDate <= :toDate)")
  List<Notification> findAllWithFiltersNoPaging(
      @Param("type") String type,
      @Param("isRead") Boolean isRead,
      @Param("fromDate") LocalDateTime fromDate,
      @Param("toDate") LocalDateTime toDate,
      Sort sort);
}
