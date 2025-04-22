package com.market.MSA.repositories.product;

import com.market.MSA.models.product.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {}
