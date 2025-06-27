package com.market.MSA.services.others;

import com.market.MSA.constants.OrderStatus;
import com.market.MSA.exceptions.AppException;
import com.market.MSA.exceptions.ErrorCode;
import com.market.MSA.mappers.others.DeliveryInfoMapper;
import com.market.MSA.models.others.DeliveryInfo;
import com.market.MSA.repositories.order.OrderRepository;
import com.market.MSA.repositories.others.DeliveryInfoRepository;
import com.market.MSA.requests.others.DeliveryInfoRequest;
import com.market.MSA.responses.others.DeliveryInfoResponse;
import java.util.Optional;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.cache.annotation.Cacheable;
import com.market.MSA.requests.filters.DeliveryInfoFilterRequest;
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
  private static final String CACHE_LIST = "all_delivery_infos";
  private static final String CACHE_PAGING = "delivery_infos_paging";
  private static final String CACHE_FILTER_LIST = "delivery_infos_filter_list";
  private static final String CACHE_FILTER_PAGING = "delivery_infos_filter_paging";
  final EntityFinderService entityFinderService;
  final DeliveryInfoRepository deliveryInfoRepository;
  final OrderRepository orderRepository;

  final DeliveryInfoMapper deliveryInfoMapper;

  // Create DeliveryInfo and set status to "delivering"
  @Transactional
  public DeliveryInfoResponse createDeliveryInfo(DeliveryInfoRequest request) {
    DeliveryInfo deliveryInfo = deliveryInfoMapper.toDeliveryInfo(request);
    deliveryInfo.setStatus(OrderStatus.DELIVERING);
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

  // Get all DeliveryInfos (List)
  @Cacheable(value = CACHE_LIST)
  public List<DeliveryInfoResponse> getAll() {
    return deliveryInfoRepository.findAll().stream()
        .map(deliveryInfoMapper::toDeliveryInfoResponse)
        .toList();
  }

  // Get all DeliveryInfos with filter (List)
  @Cacheable(value = CACHE_FILTER_LIST)
  public List<DeliveryInfoResponse> getAllDeliveryInfos(DeliveryInfoFilterRequest filter) {
    Sort sort = Sort.by("deliveryInfoId").descending();
    List<DeliveryInfo> list =
        deliveryInfoRepository.filter(
            filter.getOrderId(),
            filter.getStatus(),
            filter.getCity(),
            filter.getFromDate(),
            filter.getToDate(),
            sort);
    return list.stream().map(deliveryInfoMapper::toDeliveryInfoResponse).toList();
  }

  // Get all DeliveryInfos with filter and paging
  @Cacheable(value = CACHE_FILTER_PAGING)
  public Page<DeliveryInfoResponse> getAllDeliveryInfosWithPaging(DeliveryInfoFilterRequest filter, int page, int size) {
    Pageable pageable = PageRequest.of(page, size, Sort.by("deliveryInfoId").descending());
    Page<DeliveryInfo> pageS =
        deliveryInfoRepository.filterWithPaging(
            filter.getOrderId(),
            filter.getStatus(),
            filter.getCity(),
            filter.getFromDate(),
            filter.getToDate(),
            pageable);
    return pageS.map(deliveryInfoMapper::toDeliveryInfoResponse);
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
