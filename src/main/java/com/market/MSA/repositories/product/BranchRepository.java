package com.market.MSA.repositories.product;

import com.market.MSA.models.product.Branch;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BranchRepository extends JpaRepository<Branch, Long> {
  Optional<Branch> findByName(String name);

  @Query(
      value =
          "SELECT b.* FROM branches b "
              + "JOIN users_branches ub ON b.branch_id = ub.branches_branch_id "
              + "JOIN users u ON ub.user_user_id = u.user_id "
              + "JOIN users_roles ur ON u.user_id = ur.user_user_id "
              + "JOIN roles r ON ur.roles_role_id = r.role_id "
              + "WHERE r.name = :role",
      nativeQuery = true)
  Branch findByUserRole(@Param("role") String role);

  @Query(
      "SELECT DISTINCT b FROM Branch b "
          + "JOIN b.inventory i "
          + "JOIN i.inventoryProducts ip "
          + "WHERE ip.product.productId = :productId")
  List<Branch> findByProductId(@Param("productId") Long productId);
}
