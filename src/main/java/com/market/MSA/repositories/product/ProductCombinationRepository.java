package com.market.MSA.repositories.product;

import com.market.MSA.models.product.ProductCombination;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductCombinationRepository extends JpaRepository<ProductCombination, Long> {

  @Query(
      "SELECT pc FROM ProductCombination pc "
          + "WHERE (:productId1 IS NULL OR pc.product1.productId = :productId1) "
          + "AND (:productId2 IS NULL OR pc.product2.productId = :productId2)")
  List<ProductCombination> filter(
      @Param("productId1") Long productId1, @Param("productId2") Long productId2);

  @Query(
      "SELECT pc FROM ProductCombination pc "
          + "WHERE (:productId1 IS NULL OR pc.product1.productId = :productId1) "
          + "AND (:productId2 IS NULL OR pc.product2.productId = :productId2)")
  Page<ProductCombination> filterWithPaging(
      @Param("productId1") Long productId1,
      @Param("productId2") Long productId2,
      Pageable pageable);
}
