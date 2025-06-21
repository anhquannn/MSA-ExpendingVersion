package com.market.MSA.services.others;

import com.market.MSA.exceptions.AppException;
import com.market.MSA.exceptions.ErrorCode;
import com.market.MSA.mappers.others.DeliveryInfoMapper;
import com.market.MSA.models.others.DeliveryInfo;
import com.market.MSA.repositories.order.OrderRepository;
import com.market.MSA.repositories.others.DeliveryInfoRepository;
import com.market.MSA.requests.others.DeliveryInfoRequest;
import com.market.MSA.responses.others.DeliveryInfoResponse;
import java.util.Optional;
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
public class DeliveryInfoService {
  final EntityFinderService entityFinderService;
  final DeliveryInfoRepository deliveryInfoRepository;
  final OrderRepository orderRepository;

  final DeliveryInfoMapper deliveryInfoMapper;

  // Create DeliveryInfo and set status to "delivering"
  @Transactional
  public DeliveryInfoResponse createDeliveryInfo(DeliveryInfoRequest request) {
    DeliveryInfo deliveryInfo = deliveryInfoMapper.toDeliveryInfo(request);
    deliveryInfo.setStatus("pending");
    deliveryInfo.setOrder(
        entityFinderService.findByIdOrThrow(
            orderRepository, request.getOrderId(), ErrorCode.ORDER_NOT_FOUND));

    DeliveryInfo savedDeliveryInfo = deliveryInfoRepository.save(deliveryInfo);
    return deliveryInfoMapper.toDeliveryInfoResponse(savedDeliveryInfo);
  }

  // Update DeliveryInfo
  @Transactional
  public DeliveryInfoResponse updateDeliveryInfo(long deliveryInfoId, DeliveryInfoRequest request) {
    Optional<DeliveryInfo> existingDeliveryInfoOpt =
        deliveryInfoRepository.findById(deliveryInfoId);
    if (existingDeliveryInfoOpt.isPresent()) {
      DeliveryInfo existingDeliveryInfo = existingDeliveryInfoOpt.get();
      deliveryInfoMapper.updateDeliveryInfoFromRequest(request, existingDeliveryInfo);
      existingDeliveryInfo.setOrder(
          entityFinderService.findByIdOrThrow(
              orderRepository, request.getOrderId(), ErrorCode.ORDER_NOT_FOUND));
      DeliveryInfo updatedDeliveryInfo = deliveryInfoRepository.save(existingDeliveryInfo);
      return deliveryInfoMapper.toDeliveryInfoResponse(updatedDeliveryInfo);
    }
    throw new AppException(ErrorCode.DELIVERY_INFO_NOT_FOUND); // Or throw an exception if not found
  }

  // Delete DeliveryInfo
  @Transactional
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
