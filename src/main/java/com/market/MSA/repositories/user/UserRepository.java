package com.market.MSA.repositories.user;

import com.market.MSA.models.user.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {

  Optional<User> findByEmail(String email);

  Optional<User> findByGoogleId(String googleId);

  @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :role")
  Page<User> findByRoleWithPagination(@Param("role") String role, Pageable pageable);

  @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :role")
  List<User> findAllByRole(@Param("role") String role);

  @Query(
      "SELECT u FROM User u JOIN u.roles r JOIN u.branches b JOIN b.inventory i WHERE r.name = 'MANAGER' AND i.inventoryId = :inventoryId")
  Page<User> findManagersByInventoryIdWithPagination(
      @Param("inventoryId") Long inventoryId, Pageable pageable);

  @Query(
      "SELECT u FROM User u JOIN u.roles r JOIN u.branches b JOIN b.inventory i WHERE r.name = 'MANAGER' AND i.inventoryId = :inventoryId")
  List<User> findAllManagersByInventoryId(@Param("inventoryId") Long inventoryId);
}
