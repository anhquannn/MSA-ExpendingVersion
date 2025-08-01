package com.market.MSA.repositories.product;

import com.market.MSA.models.product.Supplier;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SupplierRepository extends JpaRepository<Supplier, Long> {
  // List - không phân trang
  @Query(
      "SELECT s FROM Supplier s WHERE "
          + "(:keyword IS NULL OR :keyword = '' OR "
          + "LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR "
          + "LOWER(s.address) LIKE LOWER(CONCAT('%', :keyword, '%')) OR "
          + "LOWER(s.contact) LIKE LOWER(CONCAT('%', :keyword, '%')))")
  @EntityGraph(attributePaths = {})
  List<Supplier> filter(@Param("keyword") String keyword);

  // Page - có phân trang
  @Query(
      "SELECT s FROM Supplier s WHERE "
          + "(:keyword IS NULL OR :keyword = '' OR "
          + "LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR "
          + "LOWER(s.address) LIKE LOWER(CONCAT('%', :keyword, '%')) OR "
          + "LOWER(s.contact) LIKE LOWER(CONCAT('%', :keyword, '%')))")
  @EntityGraph(attributePaths = {})
  Page<Supplier> filterWithPaging(@Param("keyword") String keyword, Pageable pageable);
}
