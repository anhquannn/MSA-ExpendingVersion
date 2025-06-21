package com.market.MSA.repositories.product;

import com.market.MSA.models.product.TrendingProduct;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TrendingProductRepository extends JpaRepository<TrendingProduct, Long> {

  @Query(
      "SELECT t FROM TrendingProduct t WHERE "
          + "(:productId IS NULL OR t.product.productId = :productId)")
  Page<TrendingProduct> filterWithPaging(@Param("productId") Long productId, Pageable pageable);

  @Query(
      "SELECT t FROM TrendingProduct t WHERE "
          + "(:productId IS NULL OR t.product.productId = :productId)")
  List<TrendingProduct> filter(@Param("productId") Long productId, Sort sort);

  @Modifying
  @Query("DELETE FROM TrendingProduct ")
  void deleteAllTrendingProducts();
}
