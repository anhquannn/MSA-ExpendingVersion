package com.market.MSA.repositories.product;

import com.market.MSA.models.product.OutboundTransfer;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OutboundRepository extends JpaRepository<OutboundTransfer, Long> {
  // Có phân trang
  @Query(
      "SELECT o FROM OutboundTransfer o WHERE "
          + "(:status IS NULL OR o.status = :status) AND "
          + "(:fromDate IS NULL OR o.outboundTransferDate >= :fromDate) AND "
          + "(:toDate IS NULL OR o.outboundTransferDate <= :toDate) AND "
          + "(:userId IS NULL OR o.user.userId = :userId) AND "
          + "(:inventoryId IS NULL OR o.Inventory.inventoryId = :inventoryId) AND "
          + "(:transferId IS NULL OR o.transfer.transferRequestId = :transferId)")
  Page<OutboundTransfer> filterWithPaging(
      @Param("status") String status,
      @Param("fromDate") LocalDateTime fromDate,
      @Param("toDate") LocalDateTime toDate,
      @Param("userId") Long userId,
      @Param("inventoryId") Long inventoryId,
      @Param("transferId") Long transferId,
      Pageable pageable);

  // Không phân trang
  @Query(
      "SELECT o FROM OutboundTransfer o WHERE "
          + "(:status IS NULL OR o.status = :status) AND "
          + "(:fromDate IS NULL OR o.outboundTransferDate >= :fromDate) AND "
          + "(:toDate IS NULL OR o.outboundTransferDate <= :toDate) AND "
          + "(:userId IS NULL OR o.user.userId = :userId) AND "
          + "(:inventoryId IS NULL OR o.Inventory.inventoryId = :inventoryId) AND "
          + "(:transferId IS NULL OR o.transfer.transferRequestId = :transferId)")
  List<OutboundTransfer> filter(
      @Param("status") String status,
      @Param("fromDate") LocalDateTime fromDate,
      @Param("toDate") LocalDateTime toDate,
      @Param("userId") Long userId,
      @Param("inventoryId") Long inventoryId,
      @Param("transferId") Long transferId,
      Sort sort);
}
