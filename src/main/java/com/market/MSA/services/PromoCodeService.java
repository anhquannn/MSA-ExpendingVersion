package com.market.MSA.services;

import com.market.MSA.exceptions.AppException;
import com.market.MSA.exceptions.ErrorCode;
import com.market.MSA.mappers.PromoCodeMapper;
import com.market.MSA.models.PromoCode;
import com.market.MSA.repositories.PromoCodeRepository;
import com.market.MSA.requests.PromoCodeRequest;
import com.market.MSA.responses.PromoCodeResponse;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PromoCodeService {
  PromoCodeRepository promoCodeRepository;
  PromoCodeMapper promoCodeMapper;

  // Tạo PromoCode
  @Transactional
  public PromoCodeResponse createPromoCode(PromoCodeRequest request) {
    PromoCode promoCode = promoCodeMapper.toPromoCode(request);
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
  public void deletePromoCode(Long id) {
    promoCodeRepository.deleteById(id);
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
    return promoCode;
  }

  // Lấy danh sách tất cả PromoCode
  public List<PromoCodeResponse> getAllPromoCodes() {
    return promoCodeRepository.findAll().stream()
        .map(promoCodeMapper::toPromoCodeResponse)
        .collect(Collectors.toList());
  }
}
