package com.market.MSA.services.order;

import com.market.MSA.exceptions.AppException;
import com.market.MSA.exceptions.ErrorCode;
import com.market.MSA.mappers.order.PromoCodeUsageMapper;
import com.market.MSA.models.order.Order;
import com.market.MSA.models.order.PromoCode;
import com.market.MSA.models.order.PromoCodeUsage;
import com.market.MSA.repositories.order.OrderRepository;
import com.market.MSA.repositories.order.PromoCodeRepository;
import com.market.MSA.repositories.order.PromoCodeUsageRepository;
import com.market.MSA.requests.filters.PromoCodeUsageFilterRequest;
import com.market.MSA.requests.order.PromoCodeUsageRequest;
import com.market.MSA.responses.order.PromoCodeUsageResponse;
import com.market.MSA.services.others.EntityFinderService;
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
public class PromoCodeUsageService {
  PromoCodeUsageRepository promoCodeUsageRepository;
  PromoCodeUsageMapper promoCodeUsageMapper;
  EntityFinderService entityFinderService;
  PromoCodeRepository promoCodeRepository;
  OrderRepository orderRepository;

  @Transactional
  public PromoCodeUsageResponse createPromoCodeUsage(PromoCodeUsageRequest request) {
    PromoCode promoCode =
        entityFinderService.findByIdOrThrow(
            promoCodeRepository, request.getPromoCodeId(), ErrorCode.PROMO_CODE_NOT_FOUND);

    Order order =
        entityFinderService.findByIdOrThrow(
            orderRepository, request.getOrderId(), ErrorCode.ORDER_NOT_FOUND);

    PromoCodeUsage promoCodeUsage = promoCodeUsageMapper.toUsage(request);
    promoCodeUsage.setPromoCode(promoCode);
    promoCodeUsage.setOrder(order);
    promoCodeUsage.setUser(order.getUser());

    if (request.getUsedAt() == null) {
      promoCodeUsage.setUsedAt(java.time.LocalDateTime.now());
    }

    PromoCodeUsage savedPromoCodeUsage = promoCodeUsageRepository.save(promoCodeUsage);
    return promoCodeUsageMapper.toResponse(savedPromoCodeUsage);
  }

  @Transactional
  public PromoCodeUsageResponse updatePromoCodeUsage(Long id, PromoCodeUsageRequest request) {
    PromoCodeUsage promoCodeUsage =
        promoCodeUsageRepository
            .findById(id)
            .orElseThrow(() -> new AppException(ErrorCode.PROMO_CODE_USAGE_NOT_FOUND));

    if (!promoCodeUsage.getPromoCode().getPromoCodeId().equals(request.getPromoCodeId())) {
      PromoCode promoCode =
          entityFinderService.findByIdOrThrow(
              promoCodeRepository, request.getPromoCodeId(), ErrorCode.PROMO_CODE_NOT_FOUND);
      promoCodeUsage.setPromoCode(promoCode);
    }

    if (!promoCodeUsage.getOrder().getOrderId().equals(request.getOrderId())) {
      Order order =
          entityFinderService.findByIdOrThrow(
              orderRepository, request.getOrderId(), ErrorCode.ORDER_NOT_FOUND);
      promoCodeUsage.setOrder(order);
    }

    promoCodeUsageMapper.updatePromoCodeUsage(request, promoCodeUsage);
    PromoCodeUsage updatedPromoCodeUsage = promoCodeUsageRepository.save(promoCodeUsage);
    return promoCodeUsageMapper.toResponse(updatedPromoCodeUsage);
  }

  @Transactional
  public boolean deletePromoCodeUsage(Long id) {
    if (!promoCodeUsageRepository.existsById(id)) {
      throw new AppException(ErrorCode.PROMO_CODE_USAGE_NOT_FOUND);
    }
    promoCodeUsageRepository.deleteById(id);
    return true;
  }

  public PromoCodeUsageResponse getPromoCodeUsageById(Long id) {
    PromoCodeUsage promoCodeUsage =
        promoCodeUsageRepository
            .findById(id)
            .orElseThrow(() -> new AppException(ErrorCode.PROMO_CODE_USAGE_NOT_FOUND));
    return promoCodeUsageMapper.toResponse(promoCodeUsage);
  }

  @Cacheable("all_promo_code_usages")
  public List<PromoCodeUsageResponse> getAll() {
    return promoCodeUsageRepository.findAll().stream()
        .map(promoCodeUsageMapper::toResponse)
        .collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  @Cacheable("promo_code_usages_list")
  public List<PromoCodeUsageResponse> getAllPromoCodeUsages(PromoCodeUsageFilterRequest request) {
    Sort sort = Sort.by(Sort.Direction.fromString(request.getSortDirection()), request.getSortBy());

    return promoCodeUsageRepository
        .filter(
            request.getUserId(),
            request.getPromoCodeId(),
            request.getFromDate(),
            request.getToDate(),
            sort)
        .stream()
        .map(promoCodeUsageMapper::toResponse)
        .collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  @Cacheable("promo_code_usages_paging")
  public Page<PromoCodeUsageResponse> getAllPromoCodeUsagesWithPaging(
      PromoCodeUsageFilterRequest request) {
    Sort sort = Sort.by(Sort.Direction.fromString(request.getSortDirection()), request.getSortBy());

    Pageable pageable = PageRequest.of(request.getPage() - 1, request.getPageSize(), sort);

    return promoCodeUsageRepository
        .filterWithPaging(
            request.getUserId(),
            request.getPromoCodeId(),
            request.getFromDate(),
            request.getToDate(),
            pageable)
        .map(promoCodeUsageMapper::toResponse);
  }
}
