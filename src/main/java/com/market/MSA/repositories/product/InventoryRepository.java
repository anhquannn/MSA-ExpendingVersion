package com.market.MSA.repositories.product;

import com.market.MSA.models.product.Inventory;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
  @Query("SELECT i FROM Inventory i WHERE i.branch.branchId = :branchId")
  Optional<Inventory> findByBranch_BranchId(@Param("branchId") Long branchId);

  @Query("SELECT i FROM Inventory i WHERE LOWER(i.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
  List<Inventory> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
}
