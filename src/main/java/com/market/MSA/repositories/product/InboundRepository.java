package com.market.MSA.repositories.product;

import com.market.MSA.constants.ProductStatus;
import com.market.MSA.models.product.InboundTransfer;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InboundRepository extends JpaRepository<InboundTransfer, Long> {
  // Có phân trang
  @Query(
      "SELECT i FROM InboundTransfer i WHERE "
          + "(:status IS NULL OR i.status = :status) AND "
          + "(:fromDate IS NULL OR i.inboundTransferDate >= :fromDate) AND "
          + "(:toDate IS NULL OR i.inboundTransferDate <= :toDate) AND "
          + "(:userId IS NULL OR i.user.userId = :userId) AND "
          + "(:inventoryId IS NULL OR i.inventory.inventoryId = :inventoryId) AND "
          + "(:transferId IS NULL OR i.transfer.transferRequestId = :transferId)")
  Page<InboundTransfer> filterWithPaging(
      @Param("status") ProductStatus status,
      @Param("fromDate") LocalDateTime fromDate,
      @Param("toDate") LocalDateTime toDate,
      @Param("userId") Long userId,
      @Param("inventoryId") Long inventoryId,
      @Param("transferId") Long transferId,
      Pageable pageable);

  // Không phân trang
  @Query(
      "SELECT i FROM InboundTransfer i WHERE "
          + "(:status IS NULL OR i.status = :status) AND "
          + "(:fromDate IS NULL OR i.inboundTransferDate >= :fromDate) AND "
          + "(:toDate IS NULL OR i.inboundTransferDate <= :toDate) AND "
          + "(:userId IS NULL OR i.user.userId = :userId) AND "
          + "(:inventoryId IS NULL OR i.inventory.inventoryId = :inventoryId) AND "
          + "(:transferId IS NULL OR i.transfer.transferRequestId = :transferId)")
  List<InboundTransfer> filter(
      @Param("status") ProductStatus status,
      @Param("fromDate") LocalDateTime fromDate,
      @Param("toDate") LocalDateTime toDate,
      @Param("userId") Long userId,
      @Param("inventoryId") Long inventoryId,
      @Param("transferId") Long transferId,
      Sort sort);
}
