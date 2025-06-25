package com.market.MSA.services.product;

import com.market.MSA.exceptions.AppException;
import com.market.MSA.exceptions.ErrorCode;
import com.market.MSA.mappers.product.InventoryMapper;
import com.market.MSA.models.product.Inventory;
import com.market.MSA.repositories.product.BranchRepository;
import com.market.MSA.repositories.product.InventoryRepository;
import com.market.MSA.requests.filters.InventoryFilterRequest;
import com.market.MSA.requests.product.InventoryRequest;
import com.market.MSA.responses.product.InventoryResponse;
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
public class InventoryService {
  final InventoryRepository inventoryRepository;
  final BranchRepository branchRepository;
  final EntityFinderService entityFinderService;

  final InventoryMapper inventoryMapper;

  @Transactional
  public InventoryResponse createInventory(InventoryRequest inventoryRequest) {
    Inventory inventory = inventoryMapper.toInventory(inventoryRequest);
    inventory.setBranch(
        entityFinderService.findByIdOrThrow(
            branchRepository, inventoryRequest.getBranchId(), ErrorCode.BRANCH_NOT_FOUND));
    return inventoryMapper.toInventoryResponse(inventoryRepository.save(inventory));
  }

  @Transactional
  public InventoryResponse updateInventory(Long id, InventoryRequest inventoryRequest) {
    Inventory inventory =
        inventoryRepository
            .findById(id)
            .orElseThrow(() -> new AppException(ErrorCode.INVENTORY_NOT_FOUND));
    inventory.setBranch(
        entityFinderService.findByIdOrThrow(
            branchRepository, inventoryRequest.getBranchId(), ErrorCode.BRANCH_NOT_FOUND));

    inventoryMapper.updateInventory(inventoryRequest, inventory);

    Inventory inventoryUpdated = inventoryRepository.save(inventory);
    return inventoryMapper.toInventoryResponse(inventoryUpdated);
  }

  @Transactional
  public boolean deleteInventory(Long id) {
    if (!inventoryRepository.existsById(id)) {
      throw new AppException(ErrorCode.INVENTORY_NOT_FOUND);
    }
    inventoryRepository.deleteById(id);
    return true;
  }

  @Transactional
  public InventoryResponse getInventoryById(Long id) {
    Inventory inventory =
        inventoryRepository
            .findById(id)
            .orElseThrow(() -> new AppException(ErrorCode.INVENTORY_NOT_FOUND));
    return inventoryMapper.toInventoryResponse(inventory);
  }

  @Cacheable("all_inventories")
  public List<InventoryResponse> getAll() {
    return inventoryRepository.findAll().stream()
        .map(inventoryMapper::toInventoryResponse)
        .collect(Collectors.toList());
  }

  @Cacheable("inventories_list")
  public List<InventoryResponse> getAllInventories(InventoryFilterRequest request) {
    return inventoryRepository
        .filter(request.getKeyword(), request.getBranchId(), request.getUserId())
        .stream()
        .map(inventoryMapper::toInventoryResponse)
        .collect(Collectors.toList());
  }

  @Cacheable("inventories_paging")
  public Page<InventoryResponse> getAllInventoriesWithPaging(InventoryFilterRequest request) {
    Sort sort = Sort.by(Sort.Direction.fromString(request.getSortDirection()), request.getSortBy());

    Pageable pageable = PageRequest.of(request.getPage() - 1, request.getPageSize(), sort);

    return inventoryRepository
        .filterWithPaging(
            request.getKeyword(), request.getBranchId(), request.getUserId(), pageable)
        .map(inventoryMapper::toInventoryResponse);
  }

  public InventoryResponse getInventoryByBranchId(Long branchId) {
    Inventory inventory =
        inventoryRepository
            .findByBranch_BranchId(branchId)
            .orElseThrow(() -> new AppException(ErrorCode.INVENTORY_NOT_FOUND));
    return inventoryMapper.toInventoryResponse(inventory);
  }

  @Transactional
  public void updateTotalRevenue(Long inventoryId, int quantity) {
    Inventory inventory =
        inventoryRepository
            .findById(inventoryId)
            .orElseThrow(() -> new AppException(ErrorCode.INVENTORY_NOT_FOUND));
    inventory.setTotalRevenue(inventory.getTotalRevenue() + quantity);
    inventoryRepository.save(inventory);
  }
}
