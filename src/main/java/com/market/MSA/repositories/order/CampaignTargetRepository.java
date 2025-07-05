package com.market.MSA.repositories.order;

import com.market.MSA.constants.PromoScopeType;
import com.market.MSA.models.order.CampaignTarget;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CampaignTargetRepository extends JpaRepository<CampaignTarget, Long> {
  List<CampaignTarget> findByCampaign_CampaignId(Long campaignId);

  boolean existsByCampaign_CampaignIdAndTargetTypeAndTargetId(
      Long campaignId, PromoScopeType type, Long targetId);

  // Filter with paging
  @Query(
      "SELECT ct FROM CampaignTarget ct WHERE "
          + "(:campaignId IS NULL OR ct.campaign.campaignId = :campaignId) AND "
          + "(:targetType IS NULL OR ct.targetType = :targetType) AND "
          + "(:targetId IS NULL OR ct.targetId = :targetId)")
  Page<CampaignTarget> filterWithPaging(
      @Param("campaignId") Long campaignId,
      @Param("targetType") PromoScopeType targetType,
      @Param("targetId") Long targetId,
      Pageable pageable);

  // Filter without paging
  @Query(
      "SELECT ct FROM CampaignTarget ct WHERE "
          + "(:campaignId IS NULL OR ct.campaign.campaignId = :campaignId) AND "
          + "(:targetType IS NULL OR ct.targetType = :targetType) AND "
          + "(:targetId IS NULL OR ct.targetId = :targetId)")
  List<CampaignTarget> filter(
      @Param("campaignId") Long campaignId,
      @Param("targetType") PromoScopeType targetType,
      @Param("targetId") Long targetId,
      Sort sort);
}
