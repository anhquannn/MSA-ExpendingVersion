package com.market.MSA.repositories.order;

import com.market.MSA.models.order.Campaign;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CampaignRepository extends JpaRepository<Campaign, Long> {

  @Query(
      "SELECT c FROM Campaign c WHERE (:name IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%')))")
  Page<Campaign> searchByName(@Param("name") String name, Pageable pageable);
}
