package com.market.MSA.repositories;

import com.market.MSA.models.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
  boolean existsByEmail(String email);

  Optional<User> findByEmail(String email);

  Optional<User> findByGoogleId(String googleId);
}
