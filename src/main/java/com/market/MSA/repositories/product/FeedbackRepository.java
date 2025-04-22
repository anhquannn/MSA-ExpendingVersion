package com.market.MSA.repositories.product;

import com.market.MSA.models.product.Feedback;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
  List<Feedback> findByProduct_ProductId(long productId);

  @Query("SELECT f.product.productId, AVG(f.rating) from Feedback f GROUP BY f.product.productId")
  List<Object[]> findAverageRatingsByProduct();
}
