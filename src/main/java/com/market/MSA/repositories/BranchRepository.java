package com.market.MSA.repositories;

import com.market.MSA.models.Branch;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BranchRepository extends JpaRepository<Branch, Long> {
  Optional<Branch> findByName(String name);
}
