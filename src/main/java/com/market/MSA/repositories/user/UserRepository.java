package com.market.MSA.repositories.user;

import com.market.MSA.models.user.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {

  Optional<User> findByEmail(String email);

  Optional<User> findByGoogleId(String googleId);

  @Query(
      "SELECT u FROM User u JOIN u.roles r "
          + "WHERE (:role IS NULL OR LOWER(r.name) LIKE LOWER(CONCAT('%', :role, '%')))")
  @EntityGraph(attributePaths = {"roles", "branches"})
  Page<User> findByRoleWithPagination(@Param("role") String role, Pageable pageable);

  @Query(
      "SELECT u FROM User u JOIN u.roles r "
          + "WHERE (:role IS NULL OR LOWER(r.name) LIKE LOWER(CONCAT('%', :role, '%')))")
  @EntityGraph(attributePaths = {"roles", "branches"})
  List<User> findAllByRole(@Param("role") String role);

  @Query(
      "SELECT u FROM User u JOIN u.roles r "
          + "WHERE (:role IS NULL OR LOWER(r.name) LIKE LOWER(CONCAT('%', :role, '%'))) "
          + "AND (:keyword IS NULL OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) "
          + "OR LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')) "
          + "OR LOWER(u.phoneNumber) LIKE LOWER(CONCAT('%', :keyword, '%')))")
  @EntityGraph(attributePaths = {"roles", "branches"})
  List<User> findAllByRoleAndKeyword(@Param("role") String role, @Param("keyword") String keyword);

  @Query(
      "SELECT u FROM User u JOIN u.roles r JOIN u.branches b JOIN b.inventory i WHERE r.name LIKE 'MANAGER%' AND i.inventoryId = :inventoryId")
  @EntityGraph(attributePaths = {"roles", "branches", "branches.inventory"})
  Page<User> findManagersByInventoryIdWithPagination(
      @Param("inventoryId") Long inventoryId, Pageable pageable);

  @Query(
      "SELECT u FROM User u JOIN u.roles r JOIN u.branches b JOIN b.inventory i WHERE r.name LIKE 'MANAGER%' AND i.inventoryId = :inventoryId")
  @EntityGraph(attributePaths = {"roles", "branches", "branches.inventory"})
  List<User> findAllManagersByInventoryId(@Param("inventoryId") Long inventoryId);

  @Query(
      "SELECT u FROM User u JOIN u.roles r JOIN u.branches b JOIN b.inventory i "
          + "WHERE (:inventoryId IS NULL OR i.inventoryId = :inventoryId) "
          + "AND (:role IS NULL OR r.name = :role)")
  @EntityGraph(attributePaths = {"roles", "branches", "branches.inventory"})
  List<User> findListByInventoryAndRole(
      @Param("inventoryId") Long inventoryId, @Param("role") String role);

  @Query(
      "SELECT u FROM User u JOIN u.roles r "
          + "WHERE (:role IS NULL OR LOWER(r.name) LIKE LOWER(CONCAT('%', :role, '%'))) AND "
          + "(:keyword IS NULL OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) "
          + "OR LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')) "
          + "OR LOWER(u.phoneNumber) LIKE LOWER(CONCAT('%', :keyword, '%')))")
  @EntityGraph(attributePaths = {"roles", "branches"})
  Page<User> searchByKeywordAndRole(
      @Param("keyword") String keyword, @Param("role") String role, Pageable pageable);
}
