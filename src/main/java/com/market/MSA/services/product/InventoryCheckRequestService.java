package com.market.MSA.services.product;

import com.market.MSA.constants.ProductStatus;
import com.market.MSA.exceptions.AppException;
import com.market.MSA.exceptions.ErrorCode;
import com.market.MSA.mappers.product.InventoryCheckRequestMapper;
import com.market.MSA.models.product.InventoryCheckRequest;
import com.market.MSA.repositories.product.InventoryCheckRequestRepository;
import com.market.MSA.repositories.product.InventoryRepository;
import com.market.MSA.repositories.user.UserRepository;
import com.market.MSA.requests.filters.InventoryCheckRequestFilterRequest;
import com.market.MSA.requests.product.InventoryCheckRequestRequest;
import com.market.MSA.responses.product.InventoryCheckRequestResponse;
import com.market.MSA.services.others.EntityFinderService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InventoryCheckRequestService {

  InventoryCheckRequestRepository icrRepository;
  InventoryRepository inventoryRepository;
  UserRepository userRepository;

  @Qualifier("inventoryCheckRequestMapper")
  InventoryCheckRequestMapper icrMapper;

  EntityFinderService entityFinderService;

  @Transactional
  public InventoryCheckRequestResponse create(InventoryCheckRequestRequest request) {
    InventoryCheckRequest icr = icrMapper.toInventoryCheckRequest(request);
    icr.setStatus(ProductStatus.SENT);
    icr.setRequestedDate(request.getRequestedDate());
    icr.setInventory(
        entityFinderService.findByIdOrThrow(
            inventoryRepository, request.getInventoryId(), ErrorCode.INVENTORY_NOT_FOUND));
    icr.setSurveyor(
        entityFinderService.findByIdOrThrow(
            userRepository, request.getSurveyorId(), ErrorCode.USER_NOT_EXISTED));
    icr.setUser(
        entityFinderService.findByIdOrThrow(
            userRepository, request.getUserId(), ErrorCode.USER_NOT_EXISTED));
    return icrMapper.toInventoryCheckRequestResponse(icrRepository.save(icr));
  }

  @Transactional
  public InventoryCheckRequestResponse update(Long icrId, InventoryCheckRequestRequest request) {
    InventoryCheckRequest icr =
        icrRepository
            .findById(icrId)
            .orElseThrow(() -> new AppException(ErrorCode.INVENTORY_CHECK_REQUEST_NOT_FOUND));
    // update basic fields using mapper
    icrMapper.updateInventoryCheckRequest(request, icr);
    if (request.getInventoryId() != null) {
      icr.setInventory(
          entityFinderService.findByIdOrThrow(
              inventoryRepository, request.getInventoryId(), ErrorCode.INVENTORY_NOT_FOUND));
    }
    if (request.getSurveyorId() != null) {
      icr.setSurveyor(
          entityFinderService.findByIdOrThrow(
              userRepository, request.getSurveyorId(), ErrorCode.USER_NOT_EXISTED));
    }
    if (request.getUserId() != null) {
      icr.setUser(
          entityFinderService.findByIdOrThrow(
              userRepository, request.getUserId(), ErrorCode.USER_NOT_EXISTED));
    }
    return icrMapper.toInventoryCheckRequestResponse(icrRepository.save(icr));
  }

  @Transactional
  public InventoryCheckRequestResponse updateStatusToReceived(Long icrId) {
    InventoryCheckRequest icr =
        icrRepository
            .findById(icrId)
            .orElseThrow(() -> new AppException(ErrorCode.INVENTORY_CHECK_REQUEST_NOT_FOUND));
    icr.setStatus(ProductStatus.RECEIVED);
    return icrMapper.toInventoryCheckRequestResponse(icrRepository.save(icr));
  }

  public boolean delete(Long icrId) {
    if (!icrRepository.existsById(icrId))
      throw new AppException(ErrorCode.INVENTORY_CHECK_REQUEST_NOT_FOUND);
    icrRepository.deleteById(icrId);
    return true;
  }

  public InventoryCheckRequestResponse getById(Long icrId) {
    return icrMapper.toInventoryCheckRequestResponse(
        icrRepository
            .findById(icrId)
            .orElseThrow(() -> new AppException(ErrorCode.INVENTORY_CHECK_REQUEST_NOT_FOUND)));
  }

  public List<InventoryCheckRequestResponse> filter(InventoryCheckRequestFilterRequest req) {
    Sort sort = Sort.by(Sort.Direction.fromString(req.getSortDirection()), req.getSortBy());
    return icrRepository
        .filter(req.getKeyword(), req.getInventoryId(), req.getSurveyorId(), req.getStatus())
        .stream()
        .sorted((a, b) -> 0) // simple
        .map(icrMapper::toInventoryCheckRequestResponse)
        .collect(Collectors.toList());
  }

  public Page<InventoryCheckRequestResponse> filterPaging(InventoryCheckRequestFilterRequest req) {
    Sort sort = Sort.by(Sort.Direction.fromString(req.getSortDirection()), req.getSortBy());
    Pageable pageable = PageRequest.of(req.getPage() - 1, req.getPageSize(), sort);
    return icrRepository
        .filterWithPaging(
            req.getKeyword(), req.getInventoryId(), req.getSurveyorId(), req.getStatus(), pageable)
        .map(icrMapper::toInventoryCheckRequestResponse);
  }
}
