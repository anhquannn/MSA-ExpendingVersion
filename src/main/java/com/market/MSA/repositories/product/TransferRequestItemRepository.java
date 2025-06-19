package com.market.MSA.repositories.product;

import com.market.MSA.models.product.TransferItem;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TransferRequestItemRepository extends JpaRepository<TransferItem, Long> {
  @Query(
      "SELECT ti FROM TransferItem ti WHERE "
          + "(:transferId IS NULL OR ti.transfer.transferRequestId = :transferId) AND "
          + "(:productId IS NULL OR ti.product.productId = :productId)")
  List<TransferItem> filter(
      @Param("transferId") Long transferId, @Param("productId") Long productId);

  @Query(
      "SELECT ti FROM TransferItem ti WHERE "
          + "(:transferId IS NULL OR ti.transfer.transferRequestId = :transferId) AND "
          + "(:productId IS NULL OR ti.product.productId = :productId)")
  Page<TransferItem> filterWithPaging(
      @Param("transferId") Long transferId, @Param("productId") Long productId, Pageable pageable);
}
