package com.market.MSA.repositories.order;

import com.market.MSA.models.order.ReturnOrderItem;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReturnOrderItemRepository extends JpaRepository<ReturnOrderItem, Long> {

  // Find return order items by return order
  List<ReturnOrderItem> findByReturnOrderReturnOrderId(Long returnOrderId);

  // Find return order items by order detail
  List<ReturnOrderItem> findByOrderDetailOrderDetailId(Long orderDetailId);

  // Check if an order detail already has return items
  boolean existsByOrderDetailOrderDetailId(Long orderDetailId);

  // Get total returned quantity for an order detail
  @Query(
      "SELECT COALESCE(SUM(roi.quantity), 0) FROM ReturnOrderItem roi WHERE roi.orderDetail.orderDetailId = :orderDetailId")
  int getTotalReturnedQuantityByOrderDetail(@Param("orderDetailId") Long orderDetailId);

  // Find return order items by product
  @Query("SELECT roi FROM ReturnOrderItem roi WHERE roi.orderDetail.product.productId = :productId")
  List<ReturnOrderItem> findByProductId(@Param("productId") Long productId);

  // Count return items by return order
  long countByReturnOrderReturnOrderId(Long returnOrderId);
}
