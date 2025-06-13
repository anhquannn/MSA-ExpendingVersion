package com.market.MSA.repositories.product;

import com.market.MSA.models.product.Supplier;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupplierRepository extends JpaRepository<Supplier, Long> {
  Optional<Supplier> findByName(String name);
}
