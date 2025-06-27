package com.market.MSA.repositories.product;

import com.market.MSA.constants.ProductStatus;
import com.market.MSA.models.product.Transfer;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TransferRequestRepository extends JpaRepository<Transfer, Long> {
  @Query(
      "SELECT t FROM Transfer t WHERE "
          + "(:requesterId IS NULL OR t.requester.userId = :requesterId) AND "
          + "(:approverId IS NULL OR t.approver.userId = :approverId) AND "
          + "(:fromInventoryId IS NULL OR t.fromInventory.inventoryId = :fromInventoryId) AND "
          + "(:toInventoryId IS NULL OR t.toInventory.inventoryId = :toInventoryId) AND "
          + "(:status IS NULL OR t.status = :status) AND "
          + "(:fromDate IS NULL OR t.createdAt >= :fromDate) AND "
          + "(:toDate IS NULL OR t.createdAt <= :toDate)")
  Page<Transfer> filterWithPaging(
      @Param("requesterId") Long requesterId,
      @Param("approverId") Long approverId,
      @Param("fromInventoryId") Long fromInventoryId,
      @Param("toInventoryId") Long toInventoryId,
      @Param("status") ProductStatus status,
      @Param("fromDate") LocalDateTime fromDate,
      @Param("toDate") LocalDateTime toDate,
      Pageable pageable);

  @Query(
      "SELECT t FROM Transfer t WHERE "
          + "(:requesterId IS NULL OR t.requester.userId = :requesterId) AND "
          + "(:approverId IS NULL OR t.approver.userId = :approverId) AND "
          + "(:fromInventoryId IS NULL OR t.fromInventory.inventoryId = :fromInventoryId) AND "
          + "(:toInventoryId IS NULL OR t.toInventory.inventoryId = :toInventoryId) AND "
          + "(:status IS NULL OR t.status = :status) AND "
          + "(:fromDate IS NULL OR t.createdAt >= :fromDate) AND "
          + "(:toDate IS NULL OR t.createdAt <= :toDate)")
  List<Transfer> filter(
      @Param("requesterId") Long requesterId,
      @Param("approverId") Long approverId,
      @Param("fromInventoryId") Long fromInventoryId,
      @Param("toInventoryId") Long toInventoryId,
      @Param("status") ProductStatus status,
      @Param("fromDate") LocalDateTime fromDate,
      @Param("toDate") LocalDateTime toDate,
      Sort sort);
}
