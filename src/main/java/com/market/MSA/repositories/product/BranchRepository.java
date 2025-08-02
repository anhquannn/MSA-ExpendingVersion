package com.market.MSA.repositories.product;

import com.market.MSA.models.product.Branch;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BranchRepository extends JpaRepository<Branch, Long> {
  // Không phân trang
  @Query(
      "SELECT DISTINCT b FROM Branch b "
          + "LEFT JOIN b.inventory i "
          + "LEFT JOIN i.inventoryProducts ip "
          + "LEFT JOIN b.users u "
          + "WHERE (:keyword IS NULL OR :keyword = '' OR "
          + "LOWER(b.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR "
          + "LOWER(b.city) LIKE LOWER(CONCAT('%', :keyword, '%')) OR "
          + "LOWER(b.ward) LIKE LOWER(CONCAT('%', :keyword, '%')) OR "
          + "LOWER(b.district) LIKE LOWER(CONCAT('%', :keyword, '%')) OR "
          + "LOWER(b.phone) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND "
          + "(:productId IS NULL OR ip.product.productId = :productId) AND "
          + "(:userId IS NULL OR u.userId = :userId)")
  List<Branch> filter(
      @Param("keyword") String keyword,
      @Param("productId") Long productId,
      @Param("userId") Long userId);

  // Có phân trang
  @Query(
      "SELECT DISTINCT b FROM Branch b "
          + "LEFT JOIN b.inventory i "
          + "LEFT JOIN i.inventoryProducts ip "
          + "LEFT JOIN b.users u "
          + "WHERE (:keyword IS NULL OR :keyword = '' OR "
          + "LOWER(b.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR "
          + "LOWER(b.city) LIKE LOWER(CONCAT('%', :keyword, '%')) OR "
          + "LOWER(b.ward) LIKE LOWER(CONCAT('%', :keyword, '%')) OR "
          + "LOWER(b.district) LIKE LOWER(CONCAT('%', :keyword, '%')) OR "
          + "LOWER(b.phone) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND "
          + "(:productId IS NULL OR ip.product.productId = :productId) AND "
          + "(:userId IS NULL OR u.userId = :userId)")
  Page<Branch> filterWithPaging(
      @Param("keyword") String keyword,
      @Param("productId") Long productId,
      @Param("userId") Long userId,
      Pageable pageable);

  @Query(
      value =
          "SELECT b.* FROM branches b "
              + "JOIN users_branches ub ON b.branch_id = ub.branches_branch_id "
              + "JOIN users u ON ub.user_user_id = u.user_id "
              + "JOIN users_roles ur ON u.user_id = ur.user_user_id "
              + "JOIN roles r ON ur.roles_role_id = r.role_id "
              + "WHERE r.name = :role",
      nativeQuery = true)
  @EntityGraph(attributePaths = {"inventory", "users"})
  Branch findByUserRole(@Param("role") String role);

  Optional<Branch> findByName(String name);
}
