package com.market.MSA.services;

import com.market.MSA.exceptions.AppException;
import com.market.MSA.exceptions.ErrorCode;
import com.market.MSA.mappers.DeliveryInfoMapper;
import com.market.MSA.models.DeliveryInfo;
import com.market.MSA.repositories.DeliveryInfoRepository;
import com.market.MSA.requests.DeliveryInfoRequest;
import com.market.MSA.responses.DeliveryInfoResponse;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DeliveryInfoService {
  DeliveryInfoRepository deliveryInfoRepository;
  DeliveryInfoMapper deliveryInfoMapper;

  // Create DeliveryInfo and set status to "delivering"
  public DeliveryInfoResponse createDeliveryInfo(DeliveryInfoRequest request) {
    DeliveryInfo deliveryInfo = deliveryInfoMapper.toDeliveryInfo(request);
    deliveryInfo.setStatus("delivering"); // Set status to "delivering" on creation
    DeliveryInfo savedDeliveryInfo = deliveryInfoRepository.save(deliveryInfo);
    return deliveryInfoMapper.toDeliveryInfoResponse(savedDeliveryInfo);
  }

  // Update DeliveryInfo
  public DeliveryInfoResponse updateDeliveryInfo(long deliveryInfoId, DeliveryInfoRequest request) {
    Optional<DeliveryInfo> existingDeliveryInfoOpt =
        deliveryInfoRepository.findById(deliveryInfoId);
    if (existingDeliveryInfoOpt.isPresent()) {
      DeliveryInfo existingDeliveryInfo = existingDeliveryInfoOpt.get();
      deliveryInfoMapper.updateDeliveryInfoFromRequest(request, existingDeliveryInfo);
      DeliveryInfo updatedDeliveryInfo = deliveryInfoRepository.save(existingDeliveryInfo);
      return deliveryInfoMapper.toDeliveryInfoResponse(updatedDeliveryInfo);
    }
    throw new AppException(ErrorCode.DELIVERY_INFO_NOT_FOUND); // Or throw an exception if not found
  }

  // Delete DeliveryInfo
  public boolean deleteDeliveryInfo(long deliveryInfoId) {
    Optional<DeliveryInfo> deliveryInfoOpt = deliveryInfoRepository.findById(deliveryInfoId);
    if (deliveryInfoOpt.isPresent()) {
      deliveryInfoRepository.delete(deliveryInfoOpt.get());
      return true;
    }
    throw new AppException(ErrorCode.DELIVERY_INFO_NOT_FOUND); // Or throw an exception if not found
  }

  // Get DeliveryInfo by ID
  public DeliveryInfoResponse getDeliveryInfoById(long deliveryInfoId) {
    Optional<DeliveryInfo> deliveryInfoOpt = deliveryInfoRepository.findById(deliveryInfoId);
    return deliveryInfoOpt
        .map(deliveryInfoMapper::toDeliveryInfoResponse)
        .orElseThrow(
            () -> new AppException(ErrorCode.DELIVERY_INFO_NOT_FOUND)); // Or throw an exception if
    // not found
  }
}
