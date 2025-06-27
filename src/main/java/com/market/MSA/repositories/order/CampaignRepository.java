package com.market.MSA.repositories.order;

import com.market.MSA.constants.PromocodeStatus;
import com.market.MSA.models.order.Campaign;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface CampaignRepository extends JpaRepository<Campaign, Long> {
  // Có phân trang
  @Query(
      "SELECT c FROM Campaign c WHERE "
          + "(:name IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND "
          + "(:status IS NULL OR c.status = :status) AND "
          + "(:fromDate IS NULL OR c.startDate >= :fromDate) AND "
          + "(:toDate IS NULL OR c.endDate <= :toDate) AND "
          + "(:keyword IS NULL OR "
          + "LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR "
          + "LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
  Page<Campaign> filterWithPaging(
      @Param("name") String name,
      @Param("status") PromocodeStatus status,
      @Param("fromDate") LocalDateTime fromDate,
      @Param("toDate") LocalDateTime toDate,
      @Param("keyword") String keyword,
      Pageable pageable);

  // Không phân trang
  @Query(
      "SELECT c FROM Campaign c WHERE "
          + "(:status IS NULL OR c.status = :status) AND "
          + "(:name IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND "
          + "(:fromDate IS NULL OR c.startDate >= :fromDate) AND "
          + "(:toDate IS NULL OR c.endDate <= :toDate) AND "
          + "(:keyword IS NULL OR "
          + "LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR "
          + "LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
  List<Campaign> filter(
      @Param("name") String name,
      @Param("status") PromocodeStatus status,
      @Param("fromDate") LocalDateTime fromDate,
      @Param("toDate") LocalDateTime toDate,
      @Param("keyword") String keyword,
      Sort sort);

  @Modifying
  @Transactional
  @Query(
      "UPDATE Campaign c SET c.status = 'ACTIVE' WHERE c.startDate <= :currentDate AND c.endDate > :currentDate AND c.status != 'ACTIVE'")
  void updateActiveCampaigns(LocalDateTime currentDate);

  @Modifying
  @Transactional
  @Query(
      "UPDATE Campaign c SET c.status = 'EXPIRED' WHERE c.endDate <= :currentDate AND c.status != 'EXPIRED'")
  void updateExpiredCampaigns(LocalDateTime currentDate);
}
