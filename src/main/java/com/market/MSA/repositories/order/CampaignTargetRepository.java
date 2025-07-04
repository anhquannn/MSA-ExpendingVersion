package com.market.MSA.repositories.order;

import com.market.MSA.models.order.CampaignTarget;
import com.market.MSA.constants.PromoScopeType;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CampaignTargetRepository extends JpaRepository<CampaignTarget, Long> {
  List<CampaignTarget> findByCampaign_CampaignId(Long campaignId);

  boolean existsByCampaign_CampaignIdAndTargetTypeAndTargetId(Long campaignId, PromoScopeType type, Long targetId);
}
