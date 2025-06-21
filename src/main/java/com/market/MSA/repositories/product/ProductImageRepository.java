package com.market.MSA.repositories.product;

import com.market.MSA.models.product.ProductImage;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
  @Query(
      "SELECT pi FROM ProductImage pi "
          + "WHERE pi.product.productId = :productId "
          + "ORDER BY pi.sortOrder ASC")
  List<ProductImage> filter(@Param("productId") Long productId);

  @Query(
      "SELECT pi FROM ProductImage pi "
          + "WHERE pi.product.productId = :productId "
          + "ORDER BY pi.sortOrder ASC")
  Page<ProductImage> filterWithPaging(@Param("productId") Long productId, Pageable pageable);
}
