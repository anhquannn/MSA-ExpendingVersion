package com.market.MSA.repositories;

import com.market.MSA.models.Inventory;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
  @Query("SELECT i FROM Inventory i WHERE i.branch.branchId = :branchId")
  List<Inventory> findByBranch_BranchId(@Param("branchId") Long branchId);

  @Query("SELECT i FROM Inventory i WHERE LOWER(i.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
  List<Inventory> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
}
