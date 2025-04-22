package com.market.MSA.repositories.product;

import com.market.MSA.models.product.Manufacturer;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ManufacturerRepository extends JpaRepository<Manufacturer, Long> {
  Optional<Manufacturer> findByName(String name);
}
