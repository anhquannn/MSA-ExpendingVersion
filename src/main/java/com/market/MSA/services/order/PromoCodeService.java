package com.market.MSA.services.order;

import com.market.MSA.constants.PromocodeStatus;
import com.market.MSA.exceptions.AppException;
import com.market.MSA.exceptions.ErrorCode;
import com.market.MSA.mappers.order.PromoCodeMapper;
import com.market.MSA.models.order.PromoCode;
import com.market.MSA.repositories.order.CampaignRepository;
import com.market.MSA.repositories.order.PromoCodeRepository;
import com.market.MSA.requests.order.PromoCodeRequest;
import com.market.MSA.responses.order.PromoCodeResponse;
import com.market.MSA.services.others.EntityFinderService;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PromoCodeService {
  final PromoCodeRepository promoCodeRepository;
  final PromoCodeMapper promoCodeMapper;
  private final EntityFinderService entityFinderService;
  private final CampaignRepository campaignRepository;

  // Tạo PromoCode
  @Transactional
  public PromoCodeResponse createPromoCode(PromoCodeRequest request) {
    PromoCode promoCode = promoCodeMapper.toPromoCode(request);
    promoCode.setDiscountType("percentage");
    promoCode.setStatus(PromocodeStatus.PROMO_CODE_STATUS_3.getStatus());
    promoCode.setCampaign(
        entityFinderService.findByIdOrThrow(
            campaignRepository, request.getCampaignId(), ErrorCode.CAMPAIGN_NOT_FOUND));

    PromoCode savedPromoCode = promoCodeRepository.save(promoCode);
    return promoCodeMapper.toPromoCodeResponse(savedPromoCode);
  }

  // Cập nhật PromoCode
  @Transactional
  public PromoCodeResponse updatePromoCode(Long id, PromoCodeRequest request) {
    PromoCode promoCode =
        promoCodeRepository
            .findById(id)
            .orElseThrow(() -> new AppException(ErrorCode.PROMO_CODE_NOT_FOUND));

    promoCodeMapper.updatePromoCodeFromRequest(request, promoCode);
    PromoCode updatedPromoCode = promoCodeRepository.save(promoCode);
    return promoCodeMapper.toPromoCodeResponse(updatedPromoCode);
  }

  // Xóa PromoCode
  @Transactional
  public boolean deletePromoCode(Long id) {
    if (!promoCodeRepository.existsById(id)) {
      throw new AppException(ErrorCode.PROMO_CODE_NOT_FOUND);
    }
    promoCodeRepository.deleteById(id);
    return true;
  }

  // Lấy PromoCode theo ID
  public PromoCodeResponse getPromoCodeById(Long id) {
    PromoCode promoCode =
        promoCodeRepository
            .findById(id)
            .orElseThrow(() -> new AppException(ErrorCode.PROMO_CODE_NOT_FOUND));
    return promoCodeMapper.toPromoCodeResponse(promoCode);
  }

  // Lấy PromoCode theo mã code
  public PromoCodeResponse getPromoCodeByCode(String code) {
    PromoCode promoCode =
        promoCodeRepository
            .findByCode(code)
            .orElseThrow(() -> new AppException(ErrorCode.PROMO_CODE_NOT_FOUND));
    return promoCodeMapper.toPromoCodeResponse(promoCode);
  }

  // Lấy PromoCode theo mã code
  public PromoCode findPromoCodeByCode(String code) {
    PromoCode promoCode =
        promoCodeRepository
            .findByCode(code)
            .orElseThrow(() -> new AppException(ErrorCode.PROMO_CODE_NOT_FOUND));
    validatePromoCode(promoCode);
    return promoCode;
  }

  // Lấy danh sách tất cả PromoCode
  public List<PromoCodeResponse> getAllPromoCodes(int page, int pageSize) {
    Pageable pageable = PageRequest.of(page - 1, pageSize);
    return promoCodeRepository.findAll(pageable).stream()
        .map(promoCodeMapper::toPromoCodeResponse)
        .collect(Collectors.toList());
  }

  void validatePromoCode(PromoCode promoCode) {
    Date currentDate = new Date();
    if (promoCode.getStartDate().after(currentDate)) {
      throw new AppException(ErrorCode.PROMO_CODE_NOT_YET_ACTIVE);
    }
    if (promoCode.getEndDate().before(currentDate)) {
      promoCode.setStatus(PromocodeStatus.PROMO_CODE_STATUS_2.getStatus());
      promoCodeRepository.save(promoCode);
      throw new AppException(ErrorCode.PROMO_CODE_EXPIRED);
    }
  }
}
