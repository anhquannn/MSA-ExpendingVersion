package com.market.MSA.repositories.order;

import com.market.MSA.constants.PromocodeStatus;
import com.market.MSA.models.order.PromoCode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface PromoCodeRepository extends JpaRepository<PromoCode, Long> {

  @Query(
      "SELECT p FROM PromoCode p WHERE "
          + "(:keyword IS NULL OR :keyword = '' OR "
          + "LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR "
          + "LOWER(p.code) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND "
          + "(:status IS NULL OR p.status = :status) AND "
          + "(:campaignId IS NULL OR p.campaign.campaignId = :campaignId) AND "
          + "(:fromDate IS NULL OR p.startDate >= :fromDate) AND "
          + "(:toDate IS NULL OR p.endDate <= :toDate)")
  Page<PromoCode> filterWithPaging(
      @Param("keyword") String keyword,
      @Param("status") PromocodeStatus status,
      @Param("campaignId") Long campaignId,
      @Param("fromDate") LocalDateTime fromDate,
      @Param("toDate") LocalDateTime toDate,
      Pageable pageable);

  @Query(
      "SELECT p FROM PromoCode p WHERE "
          + "(:keyword IS NULL OR :keyword = '' OR "
          + "LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR "
          + "LOWER(p.code) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND "
          + "(:status IS NULL OR p.status = :status) AND "
          + "(:campaignId IS NULL OR p.campaign.campaignId = :campaignId) AND "
          + "(:fromDate IS NULL OR p.startDate >= :fromDate) AND "
          + "(:toDate IS NULL OR p.endDate <= :toDate)")
  List<PromoCode> filter(
      @Param("keyword") String keyword,
      @Param("status") PromocodeStatus status,
      @Param("campaignId") Long campaignId,
      @Param("fromDate") LocalDateTime fromDate,
      @Param("toDate") LocalDateTime toDate,
      Sort sort);

  Optional<PromoCode> findByCode(String code);

  @Modifying
  @Transactional
  @Query(
      "UPDATE PromoCode p SET p.status = 'ACTIVE' WHERE p.startDate <= :currentDate AND p.endDate > :currentDate AND p.status != 'ACTIVE'")
  void updateActivePromoCodes(LocalDateTime currentDate);

  @Modifying
  @Transactional
  @Query(
      "UPDATE PromoCode p SET p.status = 'EXPIRED' WHERE p.endDate <= :currentDate AND p.status != 'EXPIRED'")
  void updateExpiredPromoCodes(LocalDateTime currentDate);
}
