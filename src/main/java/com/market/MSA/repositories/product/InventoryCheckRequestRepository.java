package com.market.MSA.repositories.product;

import com.market.MSA.constants.ProductStatus;
import com.market.MSA.models.product.InventoryCheckRequest;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InventoryCheckRequestRepository
    extends JpaRepository<InventoryCheckRequest, Long> {

  @Query(
      "SELECT icr FROM InventoryCheckRequest icr "
          + "WHERE (:inventoryId IS NULL OR icr.inventory.inventoryId = :inventoryId) "
          + "AND (:surveyorId IS NULL OR icr.surveyor.userId = :surveyorId) "
          + "AND (:status IS NULL OR icr.status = :status) "
          + "AND (:keyword IS NULL OR :keyword = '' OR LOWER(icr.note) LIKE LOWER(CONCAT('%', :keyword, '%')))")
  List<InventoryCheckRequest> filter(
      @Param("keyword") String keyword,
      @Param("inventoryId") Long inventoryId,
      @Param("surveyorId") Long surveyorId,
      @Param("status") ProductStatus status);

  @Query(
      "SELECT icr FROM InventoryCheckRequest icr "
          + "WHERE (:inventoryId IS NULL OR icr.inventory.inventoryId = :inventoryId) "
          + "AND (:surveyorId IS NULL OR icr.surveyor.userId = :surveyorId) "
          + "AND (:status IS NULL OR icr.status = :status) "
          + "AND (:keyword IS NULL OR :keyword = '' OR LOWER(icr.note) LIKE LOWER(CONCAT('%', :keyword, '%')))")
  Page<InventoryCheckRequest> filterWithPaging(
      @Param("keyword") String keyword,
      @Param("inventoryId") Long inventoryId,
      @Param("surveyorId") Long surveyorId,
      @Param("status") ProductStatus status,
      Pageable pageable);
}
