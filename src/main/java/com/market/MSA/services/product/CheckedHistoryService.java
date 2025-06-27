package com.market.MSA.services.product;

import com.market.MSA.exceptions.AppException;
import com.market.MSA.exceptions.ErrorCode;
import com.market.MSA.mappers.product.CheckedHistoryMapper;
import com.market.MSA.models.product.CheckedHistory;
import com.market.MSA.models.product.Inventory;
import com.market.MSA.models.user.User;
import com.market.MSA.repositories.product.CheckedHistoryRepository;
import com.market.MSA.repositories.product.InventoryRepository;
import com.market.MSA.repositories.user.UserRepository;
import com.market.MSA.requests.filters.CheckedHistoryFilterRequest;
import com.market.MSA.requests.product.CheckedHistoryRequest;
import com.market.MSA.responses.product.CheckedHistoryResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CheckedHistoryService {
  final CheckedHistoryRepository checkedHistoryRepository;
  final InventoryRepository inventoryRepository;
  final UserRepository userRepository;
  final CheckedHistoryMapper checkedHistoryMapper;

  // CREATE
  @Transactional
  @Caching(
      evict = {
        @CacheEvict(value = "all_checked_histories", allEntries = true),
        @CacheEvict(value = "checked_histories_list", allEntries = true),
        @CacheEvict(value = "checked_histories_paging", allEntries = true)
      })
  public CheckedHistoryResponse createCheckedHistory(CheckedHistoryRequest request) {
    Inventory inventory =
        inventoryRepository
            .findById(request.getInventoryId())
            .orElseThrow(() -> new AppException(ErrorCode.INVENTORY_NOT_FOUND));
    User user =
        userRepository
            .findById(request.getUserId())
            .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

    CheckedHistory checkedHistory =
        CheckedHistory.builder()
            .checkedDate(LocalDateTime.now())
            .note(request.getNote())
            .inventory(inventory)
            .user(user)
            .build();

    checkedHistory = checkedHistoryRepository.save(checkedHistory);
    return checkedHistoryMapper.toCheckedHistoryResponse(checkedHistory);
  }

  // UPDATE
  @Transactional
  @Caching(
      evict = {
        @CacheEvict(value = "all_checked_histories", allEntries = true),
        @CacheEvict(value = "checked_histories_list", allEntries = true),
        @CacheEvict(value = "checked_histories_paging", allEntries = true)
      })
  public CheckedHistoryResponse updateCheckedHistory(
      Long checkedHistoryId, CheckedHistoryRequest request) {
    CheckedHistory checkedHistory =
        checkedHistoryRepository
            .findById(checkedHistoryId)
            .orElseThrow(() -> new AppException(ErrorCode.CHECKED_HISTORY_NOT_FOUND));

    checkedHistoryMapper.updateCheckedHistory(request, checkedHistory);
    checkedHistory = checkedHistoryRepository.save(checkedHistory);
    return checkedHistoryMapper.toCheckedHistoryResponse(checkedHistory);
  }

  // DELETE
  @Transactional
  @Caching(
      evict = {
        @CacheEvict(value = "all_checked_histories", allEntries = true),
        @CacheEvict(value = "checked_histories_list", allEntries = true),
        @CacheEvict(value = "checked_histories_paging", allEntries = true)
      })
  public boolean deleteCheckedHistory(Long checkedHistoryId) {
    if (!checkedHistoryRepository.existsById(checkedHistoryId)) {
      throw new AppException(ErrorCode.CHECKED_HISTORY_NOT_FOUND);
    }
    checkedHistoryRepository.deleteById(checkedHistoryId);
    return true;
  }

  // GET by id
  public CheckedHistoryResponse getCheckedHistoryById(Long checkedHistoryId) {
    CheckedHistory checkedHistory =
        checkedHistoryRepository
            .findById(checkedHistoryId)
            .orElseThrow(() -> new AppException(ErrorCode.CHECKED_HISTORY_NOT_FOUND));
    return checkedHistoryMapper.toCheckedHistoryResponse(checkedHistory);
  }

  // GET ALL (no filter)
  @Cacheable("all_checked_histories")
  public List<CheckedHistoryResponse> getAll() {
    return checkedHistoryRepository.findAll().stream()
        .map(checkedHistoryMapper::toCheckedHistoryResponse)
        .collect(Collectors.toList());
  }

  // FILTER LIST
  @Cacheable("checked_histories_list")
  public List<CheckedHistoryResponse> getAllCheckedHistories(CheckedHistoryFilterRequest request) {
    return checkedHistoryRepository
        .filter(request.getKeyword(), request.getInventoryId(), request.getUserId())
        .stream()
        .map(checkedHistoryMapper::toCheckedHistoryResponse)
        .collect(Collectors.toList());
  }

  // FILTER PAGE
  @Cacheable("checked_histories_paging")
  public Page<CheckedHistoryResponse> getAllCheckedHistoriesWithPaging(
      CheckedHistoryFilterRequest request) {
    Sort sort = Sort.by(Sort.Direction.fromString(request.getSortDirection()), request.getSortBy());
    PageRequest pageable = PageRequest.of(request.getPage() - 1, request.getPageSize(), sort);
    return checkedHistoryRepository
        .filterWithPaging(
            request.getKeyword(), request.getInventoryId(), request.getUserId(), pageable)
        .map(checkedHistoryMapper::toCheckedHistoryResponse);
  }
}
