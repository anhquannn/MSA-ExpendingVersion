package com.market.MSA.repositories;

import com.market.MSA.models.Manufacturer;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ManufacturerRepository extends JpaRepository<Manufacturer, Long> {
  Optional<Manufacturer> findByName(String name);
}
