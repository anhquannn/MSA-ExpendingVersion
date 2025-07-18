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

    // 1. Kiểm tra đầu vào: danh sách sản phẩm không được rỗng.
    if (items == null || items.isEmpty()) {
      throw new AppException(ErrorCode.INVALID_INPUT);
    }

    double totalCost = 0.0;
    // 2. Duyệt qua từng sản phẩm để kiểm tra tồn kho và tính tổng tiền hàng.
    for (OrderItemDto item : items) {
      // Kiểm tra xem sản phẩm có đủ số lượng trong kho của chi nhánh không.
      boolean available =
          inventoryProductService.checkStockAvailability(
              branchId, item.getProductId(), item.getQuantity());
      if (!available) {
        throw new AppException(ErrorCode.INSUFFICIENT_STOCK);
      }
      // Lấy giá bán hiện tại của sản phẩm tại chi nhánh đó.
      double unitPrice =
          inventoryProductService.getBranchCurrentPrice(branchId, item.getProductId());
      // Cộng dồn vào tổng tiền hàng.
      totalCost += unitPrice * item.getQuantity();
    }

    // 3. Sử dụng hàm mới để kiểm tra và tính toán tổng số tiền được giảm giá.
    double discount = validateAndCalculateDiscount(items, promoCodes, userId, totalCost);
    // Tính tổng tiền sau khi đã trừ giảm giá.
    double grandTotal = totalCost - discount;

    // Tổng tiền không được âm.
    if (grandTotal < 0) {
      throw new AppException(ErrorCode.WRONG_PROMO_CODE);
    }

    // 4. Gọi service vận chuyển (Goship) để lấy các gói cước vận chuyển.
    List<RatesResponse> rates = goshipService.createRates(branchId, userAddressId, grandTotal);
    if (rates == null || rates.isEmpty()) {
      // Nếu không tìm thấy gói cước nào, báo lỗi.
      throw new AppException(ErrorCode.RATES_NOT_FOUND);
    }

    // 5. Lấy gói cước đầu tiên (thường là gói mặc định hoặc rẻ nhất) và cộng phí ship vào tổng
    // tiền.
    RatesResponse firstRate = rates.getFirst();
    grandTotal += firstRate.getTotalAmount();

    // 6. Xây dựng và trả về đối tượng tóm tắt đơn hàng.
    return OrderSummaryResponse.builder()
        .totalCost(totalCost) // Tổng tiền hàng gốc.
        .discount(discount) // Số tiền được giảm giá.
        .grandTotal(grandTotal) // Tổng tiền cuối cùng (đã gồm phí ship).
        .rates(firstRate) // Chi tiết gói cước vận chuyển.
        .build();
  }

  /** Kiểm tra tính hợp lệ của các mã giảm giá và tính tổng số tiền được giảm. */
  @Transactional(readOnly = true)
  public double validateAndCalculateDiscount(
      List<OrderItemDto> items, List<String> promoCodes, Long userId, double totalCost) {

    // Nếu không có mã giảm giá, trả về 0.
    if (promoCodes == null || promoCodes.isEmpty()) {
      return 0.0;
    }

    double discount = 0.0;

    // Duyệt qua từng mã để kiểm tra và tính giảm giá.
    for (String code : promoCodes) {
      // Lấy thông tin chi tiết của mã giảm giá.
      PromoCodeResponse promo = promoCodeService.getPromoCodeByCode(code, userId);

      // Kiểm tra xem đơn hàng có đủ điều kiện để áp dụng mã này không (dựa trên chiến dịch).
      boolean eligible = validatePromoCodeEligibility(promo, items, totalCost);

      if (eligible) {
        // Nếu đủ điều kiện, cộng dồn số tiền giảm giá.
        discount += totalCost * (promo.getDiscountPercentage() / 100);
      } else {
        // Nếu không, ném ra lỗi.
        throw new AppException(ErrorCode.PROMO_CODE_NOT_APPLICABLE);
      }
    }

    return discount;
  }

  /**
   * Hàm nội bộ để kiểm tra xem một đơn hàng có đủ điều kiện áp dụng khuyến mãi từ một chiến dịch
   * không.
   */
  boolean validatePromoCodeEligibility(
      PromoCodeResponse promo, List<OrderItemDto> items, double totalCost) {

    // Nếu mã giảm giá này không thuộc chiến dịch nào, nó luôn hợp lệ.
    if (promo.getCampaignResponse() == null) {
      return true;
    }

    var campaign = promo.getCampaignResponse();

    // 1. Kiểm tra giá trị đơn hàng tối thiểu.
    if (totalCost < campaign.getMinOrderValue()) {
      return false; // Không đủ điều kiện.
    }

    // 2. Kiểm tra phạm vi áp dụng của chiến dịch.
    return switch (campaign.getScopeType()) {
      case ALL -> // Áp dụng cho TẤT CẢ sản phẩm.
          true; // Áp dụng cho các sản phẩm thuộc DANH MỤC cụ thể.
        // Áp dụng cho các sản phẩm từ NHÀ CUNG CẤP cụ thể.
      case CATEGORY, SUPPLIER, BATCH_NUMBER -> {
        // Lấy danh sách ID mục tiêu (ví dụ: list of categoryId) từ CSDL.
        Set<Long> targetIds =
            campaignTargetRepository.findByCampaign_CampaignId(campaign.getCampaignId()).stream()
                .filter(t -> t.getTargetType() == campaign.getScopeType())
                .map(CampaignTarget::getTargetId)
                .collect(Collectors.toSet());

        // Yêu cầu TẤT CẢ sản phẩm trong đơn hàng đều phải thuộc phạm vi áp dụng.
        // '.allMatch()' trả về true nếu tất cả các item đều thỏa mãn điều kiện.
        yield items.stream()
            .allMatch(
                item ->
                    isItemEligibleForCampaign(
                        item,
                        campaign.getScopeType(),
                        targetIds)); // Áp dụng cho các sản phẩm theo LÔ HÀNG cụ thể.
      } // Các trường hợp khác không được hỗ trợ.
    };
  }

  /** Hàm nội bộ để kiểm tra một sản phẩm cụ thể có nằm trong phạm vi của chiến dịch không. */
  boolean isItemEligibleForCampaign(
      OrderItemDto item, PromoScopeType scopeType, Set<Long> targetIds) {

    // Sử dụng switch expression để xử lý logic gọn gàng.
    return switch (scopeType) {
      case CATEGORY -> {
        // Lấy categoryId của sản phẩm từ CSDL.
        Long catId =
            productRepository
                .findById(item.getProductId())
                .map(p -> p.getCategory().getCategoryId())
                .orElse(null);
        // Kiểm tra xem categoryId có nằm trong danh sách mục tiêu không.
        yield catId != null && targetIds.contains(catId);
      }
      case SUPPLIER -> {
        // Lấy supplierId của sản phẩm từ CSDL.
        Long supId =
            productRepository
                .findById(item.getProductId())
                .map(p -> p.getSupplier().getSupplierId())
                .orElse(null);
        // Kiểm tra xem supplierId có nằm trong danh sách mục tiêu không.
        yield supId != null && targetIds.contains(supId);
      }
        // Hiện tại chưa xử lý các scopeType khác.
      default -> false;
    };
  }
}
