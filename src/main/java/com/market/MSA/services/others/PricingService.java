package com.market.MSA.services.others;

import com.market.MSA.constants.PromoScopeType;
import com.market.MSA.dtos.order.OrderItemDto;
import com.market.MSA.exceptions.AppException;
import com.market.MSA.exceptions.ErrorCode;
import com.market.MSA.models.order.CampaignTarget;
import com.market.MSA.repositories.order.CampaignTargetRepository;
import com.market.MSA.repositories.product.ProductRepository;
import com.market.MSA.responses.goship.RatesResponse;
import com.market.MSA.responses.order.OrderSummaryResponse;
import com.market.MSA.responses.order.PromoCodeResponse;
import com.market.MSA.services.order.PromoCodeService;
import com.market.MSA.services.product.InventoryProductService;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PricingService {
  final InventoryProductService inventoryProductService;
  final PromoCodeService promoCodeService;
  final CampaignTargetRepository campaignTargetRepository;
  final ProductRepository productRepository;
  final GoshipService goshipService;

  @Transactional(readOnly = true)
  public OrderSummaryResponse calculateSummary(
      Long branchId,
      Long userAddressId,
      Long userId,
      List<OrderItemDto> items,
      List<String> promoCodes) {

    if (items == null || items.isEmpty()) {
      throw new AppException(ErrorCode.INVALID_INPUT);
    }

    double totalCost = 0.0;
    // Validate stock & compute total
    for (OrderItemDto item : items) {
      boolean available =
          inventoryProductService.checkStockAvailability(
              branchId, item.getProductId(), item.getQuantity());
      if (!available) {
        throw new AppException(ErrorCode.INSUFFICIENT_STOCK);
      }
      double unitPrice =
          inventoryProductService.getBranchCurrentPrice(branchId, item.getProductId());
      totalCost += unitPrice * item.getQuantity();
    }

    // Use the new validation method
    double discount = validateAndCalculateDiscount(items, promoCodes, userId, totalCost);
    double grandTotal = totalCost - discount;

    if (grandTotal < 0) {
      throw new AppException(ErrorCode.WRONG_PROMO_CODE);
    }

    List<RatesResponse> rates = goshipService.createRates(branchId, userAddressId, grandTotal);
    if (rates == null || rates.isEmpty()) {
      throw new AppException(ErrorCode.RATES_NOT_FOUND);
    }

    RatesResponse firstRate = rates.getFirst();
    grandTotal += firstRate.getTotalAmount();

    return OrderSummaryResponse.builder()
        .totalCost(totalCost)
        .discount(discount)
        .grandTotal(grandTotal)
        .rates(firstRate)
        .build();
  }

  @Transactional(readOnly = true)
  public double validateAndCalculateDiscount(
      List<OrderItemDto> items, List<String> promoCodes, Long userId, double totalCost) {

    if (promoCodes == null || promoCodes.isEmpty()) {
      return 0.0;
    }

    double discount = 0.0;

    for (String code : promoCodes) {
      PromoCodeResponse promo = promoCodeService.getPromoCodeByCode(code, userId);

      // Check campaign scope eligibility
      boolean eligible = validatePromoCodeEligibility(promo, items, totalCost);

      if (eligible) {
        discount += totalCost * (promo.getDiscountPercentage() / 100);
      } else {
        throw new AppException(ErrorCode.PROMO_CODE_NOT_APPLICABLE);
      }
    }

    return discount;
  }

  boolean validatePromoCodeEligibility(
      PromoCodeResponse promo, List<OrderItemDto> items, double totalCost) {

    if (promo.getCampaignResponse() == null) {
      return true; // No campaign restrictions
    }

    var campaign = promo.getCampaignResponse();

    // Check minimum order value
    if (totalCost < campaign.getMinOrderValue()) {
      return false;
    }

    // Check scope eligibility
    switch (campaign.getScopeType()) {
      case ALL -> {
        return true;
      }
      case CATEGORY, SUPPLIER, BATCH_NUMBER -> {
        Set<Long> targetIds =
            campaignTargetRepository.findByCampaign_CampaignId(campaign.getCampaignId()).stream()
                .filter(t -> t.getTargetType() == campaign.getScopeType())
                .map(CampaignTarget::getTargetId)
                .collect(Collectors.toSet());

        // Log each item's eligibility
        for (OrderItemDto item : items) {
          boolean itemEligible =
              isItemEligibleForCampaign(item, campaign.getScopeType(), targetIds);
        }

        return items.stream()
            .allMatch(item -> isItemEligibleForCampaign(item, campaign.getScopeType(), targetIds));
      }
      default -> {
        return false;
      }
    }
  }

  boolean isItemEligibleForCampaign(
      OrderItemDto item, PromoScopeType scopeType, Set<Long> targetIds) {

    return switch (scopeType) {
      case CATEGORY -> {
        Long catId =
            productRepository
                .findById(item.getProductId())
                .map(p -> p.getCategory().getCategoryId())
                .orElse(null);
        yield catId != null && targetIds.contains(catId);
      }
      case SUPPLIER -> {
        Long supId =
            productRepository
                .findById(item.getProductId())
                .map(p -> p.getSupplier().getSupplierId())
                .orElse(null);
        yield supId != null && targetIds.contains(supId);
      }
      default -> false;
    };
  }
}
