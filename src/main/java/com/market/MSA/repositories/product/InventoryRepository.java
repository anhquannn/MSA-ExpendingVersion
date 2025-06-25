package com.market.MSA.repositories.product;

import com.market.MSA.models.product.Inventory;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
  @Query(
      "SELECT DISTINCT i FROM Inventory i "
          + "LEFT JOIN i.branch b "
          + "LEFT JOIN b.users u "
          + "WHERE (:branchId IS NULL OR i.branch.branchId = :branchId) AND "
          + "(:userId IS NULL OR u.userId = :userId) AND "
          + "(:keyword IS NULL OR :keyword = '' OR "
          + "LOWER(i.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR "
          + "LOWER(i.address) LIKE LOWER(CONCAT('%', :keyword, '%')) OR "
          + "LOWER(i.contact) LIKE LOWER(CONCAT('%', :keyword, '%')))")
  Page<Inventory> filterWithPaging(
      @Param("keyword") String keyword,
      @Param("branchId") Long branchId,
      @Param("userId") Long userId,
      Pageable pageable);

  @Query(
      "SELECT DISTINCT i FROM Inventory i "
          + "LEFT JOIN i.branch b "
          + "LEFT JOIN b.users u "
          + "WHERE (:branchId IS NULL OR i.branch.branchId = :branchId) AND "
          + "(:userId IS NULL OR u.userId = :userId) AND "
          + "(:keyword IS NULL OR :keyword = '' OR "
          + "LOWER(i.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR "
          + "LOWER(i.address) LIKE LOWER(CONCAT('%', :keyword, '%')) OR "
          + "LOWER(i.contact) LIKE LOWER(CONCAT('%', :keyword, '%')))")
  List<Inventory> filter(
      @Param("keyword") String keyword,
      @Param("branchId") Long branchId,
      @Param("userId") Long userId);

  @Query("SELECT i FROM Inventory i WHERE i.branch.branchId = :branchId")
  Optional<Inventory> findByBranch_BranchId(@Param("branchId") Long branchId);
}
