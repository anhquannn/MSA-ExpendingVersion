package com.market.MSA.repositories.product;

import com.market.MSA.models.product.TrendingProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface TrendingProductRepository extends JpaRepository<TrendingProduct, Long> {

  @Modifying
  @Query("DELETE FROM TrendingProduct ")
  void deleteAllTrendingProducts();
}
