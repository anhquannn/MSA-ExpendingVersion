package com.market.MSA.repositories;

import com.market.MSA.models.InvalidatedToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvalidatedTokenRepository extends JpaRepository<InvalidatedToken, Long> {}
