package com.market.MSA.repositories.product;

import com.market.MSA.models.product.Transfer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TransferRequestRepository extends JpaRepository<Transfer, Long> {
  @Query("SELECT t FROM Transfer t WHERE t.fromInventory.inventoryId = :inventoryId")
  Page<Transfer> findByInventory_FromInventoryIdWithPageable(
      @Param("fromInventoryId") Long inventoryId, Pageable pageable);

  @Query("SELECT t FROM Transfer t WHERE t.toInventory.inventoryId = :inventoryId")
  Page<Transfer> findByInventory_ToInventoryIdWithPageable(
      @Param("toInventoryId") Long inventoryId, Pageable pageable);

  @Query("SELECT t FROM Transfer t WHERE t.requester.userId = :userId")
  Page<Transfer> findByUser_RequesterIdWithPageable(
      @Param("requesterId") Long userId, Pageable pageable);

  @Query("SELECT t FROM Transfer t WHERE t.approver.userId = :userId")
  Page<Transfer> findByUser_ApproverIdWithPageable(
      @Param("approverId") Long userId, Pageable pageable);
}
