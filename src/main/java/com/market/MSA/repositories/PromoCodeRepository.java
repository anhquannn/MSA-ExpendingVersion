package com.market.MSA.repositories;

import com.market.MSA.models.PromoCode;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PromoCodeRepository extends JpaRepository<PromoCode, Long> {

  Optional<PromoCode> findByCode(String code);
}
