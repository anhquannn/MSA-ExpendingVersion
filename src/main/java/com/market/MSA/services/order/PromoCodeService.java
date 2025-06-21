package com.market.MSA.services.order;

import com.market.MSA.constants.PromocodeStatus;
import com.market.MSA.exceptions.AppException;
import com.market.MSA.exceptions.ErrorCode;
import com.market.MSA.mappers.order.PromoCodeMapper;
import com.market.MSA.models.order.PromoCode;
import com.market.MSA.repositories.order.CampaignRepository;
import com.market.MSA.repositories.order.PromoCodeRepository;
import com.market.MSA.repositories.order.PromoCodeUsageRepository;
import com.market.MSA.requests.filters.PromoCodeFilterRequest;
import com.market.MSA.requests.order.PromoCodeRequest;
import com.market.MSA.responses.order.OrderResponse;
import com.market.MSA.responses.order.PromoCodeResponse;
import com.market.MSA.services.others.EntityFinderService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
  private final PromoCodeUsageRepository promoCodeUsageRepository;

  // Tạo PromoCode
  @Transactional
  public PromoCodeResponse createPromoCode(PromoCodeRequest request) {
    PromoCode promoCode = promoCodeMapper.toPromoCode(request);
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
  public PromoCodeResponse getPromoCodeByCode(String code, Long userId) {
    PromoCode promoCode = findPromoCodeByCode(code);

    // Check if user has already used this promo code
    if (userId != null && hasUserUsedPromoCode(userId, promoCode.getPromoCodeId())) {
      throw new AppException(ErrorCode.PROMO_CODE_ALREADY_USED);
    }

    return promoCodeMapper.toPromoCodeResponse(promoCode);
  }

  public boolean hasUserUsedPromoCode(Long userId, Long promoCodeId) {
    return promoCodeUsageRepository.existsByUser_UserIdAndPromoCode_PromoCodeId(
        userId, promoCodeId);
  }

  public List<PromoCodeResponse> filterUsedPromoCodes(
      List<PromoCodeResponse> promoCodes, Long userId) {
    if (userId == null) {
      return promoCodes;
    }

    return promoCodes.stream()
        .filter(promo -> !hasUserUsedPromoCode(userId, promo.getPromoCodeId()))
        .collect(Collectors.toList());
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

  @Cacheable("all_promo_codes")
  public List<PromoCodeResponse> getAll() {
    return promoCodeRepository.findAll().stream().map(promoCodeMapper::toPromoCodeResponse).collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  @Cacheable("promo_codes")
  public List<PromoCodeResponse> getAllPromoCodes(PromoCodeFilterRequest request, Long userId) {
    Sort sort = Sort.by(Sort.Direction.fromString(request.getSortDirection()), request.getSortBy());

    List<PromoCodeResponse> promoCodes =
        promoCodeRepository
            .filter(
                request.getName(),
                request.getStatus(),
                request.getCode(),
                request.getCampaignId(),
                request.getFromDate(),
                request.getToDate(),
                sort)
            .stream()
            .map(promoCodeMapper::toPromoCodeResponse)
            .collect(Collectors.toList());

    return filterUsedPromoCodes(promoCodes, userId);
  }

  @Transactional(readOnly = true)
  @Cacheable("promo_codes")
  public Page<PromoCodeResponse> getAllPromoCodesWithPaging(
      PromoCodeFilterRequest request, Long userId) {
    Sort sort = Sort.by(Sort.Direction.fromString(request.getSortDirection()), request.getSortBy());

    Pageable pageable = PageRequest.of(request.getPage() - 1, request.getPageSize(), sort);

    Page<PromoCodeResponse> promoCodePage =
        promoCodeRepository
            .filterWithPaging(
                request.getName(),
                request.getStatus(),
                request.getCode(),
                request.getCampaignId(),
                request.getFromDate(),
                request.getToDate(),
                pageable)
            .map(promoCodeMapper::toPromoCodeResponse);

    // Apply user-specific filtering
    if (userId != null) {
      List<PromoCodeResponse> filteredContent =
          filterUsedPromoCodes(promoCodePage.getContent(), userId);
      return new org.springframework.data.domain.PageImpl<>(
          filteredContent, pageable, promoCodePage.getTotalElements());
    }

    return promoCodePage;
  }

  void validatePromoCode(PromoCode promoCode) {
    LocalDateTime currentDate = LocalDateTime.now();
    if (promoCode.getStartDate().isAfter(currentDate)) {
      throw new AppException(ErrorCode.PROMO_CODE_NOT_YET_ACTIVE);
    }
    if (promoCode.getEndDate().isBefore(currentDate)) {
      promoCode.setStatus(PromocodeStatus.PROMO_CODE_STATUS_2.getStatus());
      promoCodeRepository.save(promoCode);
      throw new AppException(ErrorCode.PROMO_CODE_EXPIRED);
    }
  }
}
