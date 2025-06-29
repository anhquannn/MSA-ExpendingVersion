package com.market.MSA.repositories.others;

import com.market.MSA.models.others.DeviceToken;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeviceTokenRepository extends JpaRepository<DeviceToken, Long> {
  List<DeviceToken> findAllByUserId(Long userId);

  Optional<DeviceToken> findByToken(String token);
}
