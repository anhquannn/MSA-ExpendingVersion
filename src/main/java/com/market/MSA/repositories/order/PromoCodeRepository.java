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

  // Enhanced query with cartId filter
  @Query(
      "SELECT DISTINCT p FROM PromoCode p "
          + "LEFT JOIN p.campaign c "
          + "WHERE "
          + "(:keyword IS NULL OR :keyword = '' OR "
          + "LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR "
          + "LOWER(p.code) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND "
          + "(:status IS NULL OR p.status = :status) AND "
          + "(:campaignId IS NULL OR c.campaignId = :campaignId) AND "
          + "(:fromDate IS NULL OR p.startDate >= :fromDate) AND "
          + "(:toDate IS NULL OR p.endDate <= :toDate) AND ("
          + "  :cartId IS NULL OR "
          + "  c IS NULL OR "
          + "  c.scopeType = 'ALL' OR "
          + "  ("
          + "    c.scopeType = 'CATEGORY' AND "
          + "    ("
          + "      SELECT COUNT(ci) FROM CartItem ci "
          + "      JOIN ci.product p2 "
          + "      JOIN CampaignTarget ct2 ON ct2.campaign.campaignId = c.campaignId "
          + "      WHERE ci.cart.cartId = :cartId AND ci.isSelected = true "
          + "        AND ct2.targetType = 'CATEGORY' "
          + "        AND ct2.targetId = p2.category.categoryId"
          + "    ) = ("
          + "      SELECT COUNT(ci) FROM CartItem ci "
          + "      WHERE ci.cart.cartId = :cartId AND ci.isSelected = true"
          + "    )"
          + "  ) OR "
          + "  ("
          + "    c.scopeType = 'SUPPLIER' AND "
          + "    ("
          + "      SELECT COUNT(ci) FROM CartItem ci "
          + "      JOIN ci.product p3 "
          + "      JOIN CampaignTarget ct3 ON ct3.campaign.campaignId = c.campaignId "
          + "      WHERE ci.cart.cartId = :cartId AND ci.isSelected = true "
          + "        AND ct3.targetType = 'SUPPLIER' "
          + "        AND ct3.targetId = p3.supplier.supplierId"
          + "    ) = ("
          + "      SELECT COUNT(ci) FROM CartItem ci "
          + "      WHERE ci.cart.cartId = :cartId AND ci.isSelected = true"
          + "    )"
          + "  )"
          + ")")
  Page<PromoCode> filterWithPagingAndCart(
      @Param("keyword") String keyword,
      @Param("status") PromocodeStatus status,
      @Param("campaignId") Long campaignId,
      @Param("fromDate") LocalDateTime fromDate,
      @Param("toDate") LocalDateTime toDate,
      @Param("cartId") Long cartId,
      Pageable pageable);

  // Enhanced query with cartId filter (List version)
  @Query(
      "SELECT DISTINCT p FROM PromoCode p "
          + "LEFT JOIN p.campaign c "
          + "WHERE "
          + "(:keyword IS NULL OR :keyword = '' OR "
          + "LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR "
          + "LOWER(p.code) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND "
          + "(:status IS NULL OR p.status = :status) AND "
          + "(:campaignId IS NULL OR c.campaignId = :campaignId) AND "
          + "(:fromDate IS NULL OR p.startDate >= :fromDate) AND "
          + "(:toDate IS NULL OR p.endDate <= :toDate) AND ("
          + "  :cartId IS NULL OR "
          + "  c IS NULL OR "
          + "  c.scopeType = 'ALL' OR "
          + "  ("
          + "    c.scopeType = 'CATEGORY' AND "
          + "    ("
          + "      SELECT COUNT(ci) FROM CartItem ci "
          + "      JOIN ci.product p2 "
          + "      JOIN CampaignTarget ct2 ON ct2.campaign.campaignId = c.campaignId "
          + "      WHERE ci.cart.cartId = :cartId AND ci.isSelected = true "
          + "        AND ct2.targetType = 'CATEGORY' "
          + "        AND ct2.targetId = p2.category.categoryId"
          + "    ) = ("
          + "      SELECT COUNT(ci) FROM CartItem ci "
          + "      WHERE ci.cart.cartId = :cartId AND ci.isSelected = true"
          + "    )"
          + "  ) OR "
          + "  ("
          + "    c.scopeType = 'SUPPLIER' AND "
          + "    ("
          + "      SELECT COUNT(ci) FROM CartItem ci "
          + "      JOIN ci.product p3 "
          + "      JOIN CampaignTarget ct3 ON ct3.campaign.campaignId = c.campaignId "
          + "      WHERE ci.cart.cartId = :cartId AND ci.isSelected = true "
          + "        AND ct3.targetType = 'SUPPLIER' "
          + "        AND ct3.targetId = p3.supplier.supplierId"
          + "    ) = ("
          + "      SELECT COUNT(ci) FROM CartItem ci "
          + "      WHERE ci.cart.cartId = :cartId AND ci.isSelected = true"
          + "    )"
          + "  )"
          + ")")
  List<PromoCode> filterWithCart(
      @Param("keyword") String keyword,
      @Param("status") PromocodeStatus status,
      @Param("campaignId") Long campaignId,
      @Param("fromDate") LocalDateTime fromDate,
      @Param("toDate") LocalDateTime toDate,
      @Param("cartId") Long cartId,
      Sort sort);

  // Simplified version if you prefer to keep the original queries and add new ones
  @Query(
      "SELECT DISTINCT p FROM PromoCode p "
          + "WHERE p.status = :status "
          + "AND (:cartId IS NULL OR "
          + "     p.campaign IS NULL OR "
          + "     p.campaign.scopeType = 'ALL' OR "
          + "     ("
          + "       p.campaign.scopeType = 'CATEGORY' AND "
          + "       ("
          + "         SELECT COUNT(ci) FROM CartItem ci "
          + "         JOIN ci.product prod "
          + "         JOIN CampaignTarget ct ON ct.campaign.campaignId = p.campaign.campaignId "
          + "         WHERE ci.cart.cartId = :cartId AND ci.isSelected = true "
          + "           AND ct.targetType = 'CATEGORY' AND ct.targetId = prod.category.categoryId"
          + "       ) = ("
          + "         SELECT COUNT(ci) FROM CartItem ci "
          + "         WHERE ci.cart.cartId = :cartId AND ci.isSelected = true"
          + "       )"
          + "     ) OR "
          + "     ("
          + "       p.campaign.scopeType = 'SUPPLIER' AND "
          + "       ("
          + "         SELECT COUNT(ci) FROM CartItem ci "
          + "         JOIN ci.product prod "
          + "         JOIN CampaignTarget ct ON ct.campaign.campaignId = p.campaign.campaignId "
          + "         WHERE ci.cart.cartId = :cartId AND ci.isSelected = true "
          + "           AND ct.targetType = 'SUPPLIER' AND ct.targetId = prod.supplier.supplierId"
          + "       ) = ("
          + "         SELECT COUNT(ci) FROM CartItem ci "
          + "         WHERE ci.cart.cartId = :cartId AND ci.isSelected = true"
          + "       )"
          + "     )"
          + ")")
  List<PromoCode> findApplicablePromoCodesForCart(
      @Param("cartId") Long cartId, @Param("status") PromocodeStatus status);

  // For getting all active promo codes for a cart
  @Query(
      "SELECT DISTINCT p FROM PromoCode p "
          + "WHERE p.status = 'ACTIVE' "
          + "AND p.startDate <= CURRENT_TIMESTAMP "
          + "AND p.endDate >= CURRENT_TIMESTAMP "
          + "AND (:cartId IS NULL OR "
          + "     p.campaign IS NULL OR "
          + "     p.campaign.scopeType = 'ALL' OR "
          + "     ("
          + "       p.campaign.scopeType = 'CATEGORY' AND "
          + "       ("
          + "         SELECT COUNT(ci) FROM CartItem ci "
          + "         JOIN ci.product prod "
          + "         JOIN CampaignTarget ct ON ct.campaign.campaignId = p.campaign.campaignId "
          + "         WHERE ci.cart.cartId = :cartId AND ci.isSelected = true "
          + "           AND ct.targetType = 'CATEGORY' AND ct.targetId = prod.category.categoryId"
          + "       ) = ("
          + "         SELECT COUNT(ci) FROM CartItem ci "
          + "         WHERE ci.cart.cartId = :cartId AND ci.isSelected = true"
          + "       )"
          + "     ) OR "
          + "     ("
          + "       p.campaign.scopeType = 'SUPPLIER' AND "
          + "       ("
          + "         SELECT COUNT(ci) FROM CartItem ci "
          + "         JOIN ci.product prod "
          + "         JOIN CampaignTarget ct ON ct.campaign.campaignId = p.campaign.campaignId "
          + "         WHERE ci.cart.cartId = :cartId AND ci.isSelected = true "
          + "           AND ct.targetType = 'SUPPLIER' AND ct.targetId = prod.supplier.supplierId"
          + "       ) = ("
          + "         SELECT COUNT(ci) FROM CartItem ci "
          + "         WHERE ci.cart.cartId = :cartId AND ci.isSelected = true"
          + "       )"
          + "     )"
          + ")")
  List<PromoCode> findActivePromoCodesForCart(@Param("cartId") Long cartId);

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
