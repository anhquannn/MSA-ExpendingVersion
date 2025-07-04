package com.market.MSA.services.others;

import com.market.MSA.dtos.order.OrderItemDto;
import com.market.MSA.exceptions.AppException;
import com.market.MSA.exceptions.ErrorCode;
import com.market.MSA.repositories.order.CampaignTargetRepository;
import com.market.MSA.repositories.product.ProductRepository;
import com.market.MSA.responses.goship.RatesResponse;
import com.market.MSA.responses.order.OrderSummaryResponse;
import com.market.MSA.responses.order.PromoCodeResponse;
import com.market.MSA.services.order.PromoCodeService;
import com.market.MSA.services.product.InventoryProductService;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    double discount = 0.0;
    double grandTotal = totalCost;

    if (promoCodes != null && !promoCodes.isEmpty()) {
      for (String code : promoCodes) {
        PromoCodeResponse promo = promoCodeService.getPromoCodeByCode(code, userId);
        // Check campaign scope eligibility
        boolean eligible = true;
        if (promo.getCampaignResponse() != null) {
          var campaign = promo.getCampaignResponse();
          switch (campaign.getScopeType()) {
            case ALL -> eligible = true;
            case CATEGORY, SUPPLIER, BATCH_NUMBER -> {
              java.util.Set<Long> targetIds =
                  campaignTargetRepository
                      .findByCampaign_CampaignId(campaign.getCampaignId())
                      .stream()
                      .filter(t -> t.getTargetType() == campaign.getScopeType())
                      .map(com.market.MSA.models.order.CampaignTarget::getTargetId)
                      .collect(java.util.stream.Collectors.toSet());
              for (OrderItemDto item : items) {
                // Depending on scope evaluate
                switch (campaign.getScopeType()) {
                  case CATEGORY -> {
                    Long catId =
                        productRepository
                            .findById(item.getProductId())
                            .map(p -> p.getCategory().getCategoryId())
                            .orElse(null);
                    if (catId == null || !targetIds.contains(catId)) eligible = false;
                  }
                  case SUPPLIER -> {
                    Long supId =
                        productRepository
                            .findById(item.getProductId())
                            .map(p -> p.getSupplier().getSupplierId())
                            .orElse(null);
                    if (supId == null || !targetIds.contains(supId)) eligible = false;
                  }

                  case BATCH_NUMBER -> {
                    // batch scope handled separately when creating order detail, skip for preview
                  }
                  default -> {}
                }
                if (!eligible) break;
              }
            }
          }
          if (totalCost < campaign.getMinOrderValue()) eligible = false;
        }
        if (eligible) {
          discount += totalCost * (promo.getDiscountPercentage() / 100);
        }
      }
      grandTotal -= discount;
    }

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
}
