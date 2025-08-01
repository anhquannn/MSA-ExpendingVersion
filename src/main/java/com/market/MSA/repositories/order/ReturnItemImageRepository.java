package com.market.MSA.repositories.order;

import com.market.MSA.models.order.ReturnItemImage;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReturnItemImageRepository extends JpaRepository<ReturnItemImage, Long> {

  // Find images by return order item
  List<ReturnItemImage> findByReturnOrderItemItemId(Long itemId);

  // Find images by return order
  @Query(
      "SELECT rii FROM ReturnItemImage rii WHERE rii.returnOrderItem.returnOrder.returnOrderId = :returnOrderId")
  List<ReturnItemImage> findByReturnOrderId(@Param("returnOrderId") Long returnOrderId);

  // Count images by return order item
  long countByReturnOrderItemItemId(Long itemId);

  // Delete images by return order item
  void deleteByReturnOrderItemItemId(Long itemId);
}
