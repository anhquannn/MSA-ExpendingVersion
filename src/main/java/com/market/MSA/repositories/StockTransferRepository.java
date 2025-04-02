package com.market.MSA.repositories;

import com.market.MSA.models.StockTransfer;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StockTransferRepository extends JpaRepository<StockTransfer, Long> {
  @Query(
      "SELECT s FROM StockTransfer s WHERE s.product.productId = :productId AND s.status = :status")
  List<StockTransfer> findByProduct_ProductId(
      @Param("productId") Long productId, @Param("status") String status);

  @Query(
      "SELECT s FROM StockTransfer s WHERE s.fromBranch.branchId = :fromBranchId AND s.status = :status")
  List<StockTransfer> findByFromBranch_FromBranchId(
      @Param("fromBranchId") Long fromBranchId, @Param("status") String status);

  @Query(
      "SELECT s FROM StockTransfer s WHERE s.toBranch.branchId = :toBranchId AND s.status = :status")
  List<StockTransfer> findByToBranch_ToBranchId(
      @Param("toBranchId") Long toBranchId, @Param("status") String status);

  @Query(
      "SELECT s FROM StockTransfer s WHERE s.userRequest.userId = :userRequestId AND s.status = :status")
  List<StockTransfer> findByUserRequest_UserRequestId(
      @Param("userRequestId") Long userRequestId, @Param("status") String status);

  @Query(
      "SELECT s FROM StockTransfer s WHERE s.userResponse.userId = :userResponseId AND s.status = :status")
  List<StockTransfer> findByUserResponse_UserResponseId(
      @Param("userResponseId") Long userResponseId, @Param("status") String status);
}
