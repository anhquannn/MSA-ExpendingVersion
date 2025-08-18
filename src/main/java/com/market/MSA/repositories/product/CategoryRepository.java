package com.market.MSA.repositories.product;

import com.market.MSA.models.product.Category;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CategoryRepository extends JpaRepository<Category, Long> {
  @Query(
      "SELECT c FROM Category c WHERE "
          + "(:name IS NULL OR :name = '' OR LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND "
          + "(:parentId IS NULL OR c.parentCategory.categoryId = :parentId)")
  List<Category> filter(@Param("name") String name, @Param("parentId") Long parentId);

  @Query(
      "SELECT c FROM Category c WHERE "
          + "(:name IS NULL OR :name = '' OR LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND "
          + "(:parentId IS NULL OR c.parentCategory.categoryId = :parentId)")
  Page<Category> filterWithPaging(
      @Param("name") String name, @Param("parentId") Long parentId, Pageable pageable);
}
