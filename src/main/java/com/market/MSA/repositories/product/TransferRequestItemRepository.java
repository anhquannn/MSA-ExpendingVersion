package com.market.MSA.repositories.product;

import com.market.MSA.models.product.TransferItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TransferRequestItemRepository extends JpaRepository<TransferItem, Long> {
  @Query("SELECT t FROM TransferItem t WHERE t.transfer.transferRequestId = :transferRequestId")
  Page<TransferItem> findByTransferRequest_TransferRequestIdWithPageable(
      @Param("transferRequestId") Long transferRequestId, Pageable pageable);

  @Query("SELECT t FROM TransferItem t WHERE t.product.productId = :productId")
  Page<TransferItem> findByProduct_ProductIdWithPageable(
      @Param("productId") Long productId, Pageable pageable);
}
