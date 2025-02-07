package com.market.MSA.services;

import com.market.MSA.exceptions.AppException;
import com.market.MSA.exceptions.ErrorCode;
import com.market.MSA.mappers.DeliveryDetailMapper;
import com.market.MSA.models.DeliveryDetail;
import com.market.MSA.repositories.DeliveryDetailRepository;
import com.market.MSA.requests.DeliveryDetailRequest;
import com.market.MSA.responses.DeliveryDetailResponse;
import java.util.List;
import java.util.Optional;
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
public class DeliveryDetailService {
  DeliveryDetailRepository deliveryDetailRepository;
  DeliveryDetailMapper deliveryDetailMapper;

  @Transactional
  public DeliveryDetailResponse createDeliveryDetail(DeliveryDetailRequest request) {
    DeliveryDetail deliveryDetail = deliveryDetailMapper.toDeliveryDetail(request);
    deliveryDetailRepository.save(deliveryDetail);
    return deliveryDetailMapper.toDeliveryDetailResponse(deliveryDetail);
  }

  @Transactional
  public DeliveryDetailResponse updateDeliveryDetail(Long id, DeliveryDetailRequest request) {
    Optional<DeliveryDetail> existingDeliveryDetail = deliveryDetailRepository.findById(id);
    if (existingDeliveryDetail.isPresent()) {
      DeliveryDetail deliveryDetail = existingDeliveryDetail.get();
      deliveryDetailMapper.updateDeliveryDetailFromRequest(request, deliveryDetail);
      deliveryDetailRepository.save(deliveryDetail);
      return deliveryDetailMapper.toDeliveryDetailResponse(deliveryDetail);
    } else {
      throw new AppException(ErrorCode.DELIVERY_DETAIL_NOT_FOUND);
    }
  }

  @Transactional
  public void deleteDeliveryDetail(Long id) {
    Optional<DeliveryDetail> deliveryDetail = deliveryDetailRepository.findById(id);
    if (deliveryDetail.isPresent()) {
      deliveryDetailRepository.delete(deliveryDetail.get());
    } else {
      throw new AppException(ErrorCode.DELIVERY_DETAIL_NOT_FOUND);
    }
  }

  public DeliveryDetailResponse getDeliveryDetailById(Long id) {
    Optional<DeliveryDetail> deliveryDetail = deliveryDetailRepository.findById(id);
    if (deliveryDetail.isPresent()) {
      return deliveryDetailMapper.toDeliveryDetailResponse(deliveryDetail.get());
    } else {
      throw new AppException(ErrorCode.DELIVERY_DETAIL_NOT_FOUND);
    }
  }

  public List<DeliveryDetailResponse> getAllDeliveryDetails(int page, int pageSize) {
    List<DeliveryDetail> deliveryDetails = deliveryDetailRepository.findAll();
    return deliveryDetails.stream()
        .map(deliveryDetailMapper::toDeliveryDetailResponse)
        .collect(Collectors.toList());
  }

  public List<DeliveryDetailResponse> getAllDeliveryDetailsByDeliveryInfoId(
      Long deliveryInfoId, int page, int pageSize) {
    List<DeliveryDetail> deliveryDetails =
        deliveryDetailRepository.findByDeliveryInfo_DeliveryInfoId(deliveryInfoId);
    return deliveryDetails.stream()
        .map(deliveryDetailMapper::toDeliveryDetailResponse)
        .collect(Collectors.toList());
  }
}
