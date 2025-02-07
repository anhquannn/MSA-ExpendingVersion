package com.market.MSA.repositories;

import com.market.MSA.models.Feedback;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
  List<Feedback> findByProduct_ProductId(long productId);
}
