package com.market.MSA.services.user;

import com.market.MSA.exceptions.AppException;
import com.market.MSA.exceptions.ErrorCode;
import com.market.MSA.mappers.user.UserBehaviorMapper;
import com.market.MSA.models.user.UserBehavior;
import com.market.MSA.repositories.product.ProductRepository;
import com.market.MSA.repositories.user.UserBehaviorRepository;
import com.market.MSA.repositories.user.UserRepository;
import com.market.MSA.requests.filters.UserBehaviorFilterRequest;
import com.market.MSA.requests.user.UserBehaviorRequest;
import com.market.MSA.responses.user.RewardPointTransactionResponse;
import com.market.MSA.responses.user.UserBehaviorResponse;
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
public class UserBehaviorService {

  final UserBehaviorRepository userBehaviorRepository;
  final UserBehaviorMapper userBehaviorMapper;
  final EntityFinderService entityFinderService;
  final ProductRepository productRepository;
  final UserRepository userRepository;

  // Tạo UserBehavior
  @Transactional
  public UserBehaviorResponse createUserBehavior(UserBehaviorRequest request) {
    UserBehavior userBehavior = userBehaviorMapper.toUserBehavior(request);
    userBehavior.setProduct(
        entityFinderService.findByIdOrThrow(
            productRepository, request.getProductId(), ErrorCode.PRODUCT_NOT_FOUND));
    userBehavior.setUser(
        entityFinderService.findByIdOrThrow(
            userRepository, request.getUserId(), ErrorCode.USER_NOT_EXISTED));

    UserBehavior savedUserBehavior = userBehaviorRepository.save(userBehavior);
    return userBehaviorMapper.toUserBehaviorResponse(savedUserBehavior);
  }

  // Cập nhật UserBehavior
  @Transactional
  public UserBehaviorResponse updateUserBehavior(Long id, UserBehaviorRequest request) {
    UserBehavior userBehavior =
        userBehaviorRepository
            .findById(id)
            .orElseThrow(() -> new AppException(ErrorCode.USER_BEHAVIOR_NOT_FOUND));
    userBehavior.setProduct(
        entityFinderService.findByIdOrThrow(
            productRepository, request.getProductId(), ErrorCode.PRODUCT_NOT_FOUND));
    userBehavior.setUser(
        entityFinderService.findByIdOrThrow(
            userRepository, request.getUserId(), ErrorCode.USER_NOT_EXISTED));

    userBehaviorMapper.updateUserBehaviorFromRequest(request, userBehavior);
    UserBehavior updatedUserBehavior = userBehaviorRepository.save(userBehavior);
    return userBehaviorMapper.toUserBehaviorResponse(updatedUserBehavior);
  }

  // Xóa UserBehavior
  @Transactional
  public boolean deleteUserBehavior(Long id) {
    if (!userBehaviorRepository.existsById(id)) {
      throw new AppException(ErrorCode.USER_BEHAVIOR_NOT_FOUND);
    }
    userBehaviorRepository.deleteById(id);
    return true;
  }

  // Lấy UserBehavior theo ID
  public UserBehaviorResponse getUserBehaviorById(Long id) {
    UserBehavior userBehavior =
        userBehaviorRepository
            .findById(id)
            .orElseThrow(() -> new AppException(ErrorCode.USER_BEHAVIOR_NOT_FOUND));
    return userBehaviorMapper.toUserBehaviorResponse(userBehavior);
  }

  @Cacheable("all_user_behaviors")
  public List<UserBehaviorResponse> getAll() {
    return userBehaviorRepository.findAll().stream().map(userBehaviorMapper::toUserBehaviorResponse).collect(Collectors.toList());
  }

  @Cacheable("user_behaviors_list")
  public List<UserBehaviorResponse> getAllUserBehaviors(UserBehaviorFilterRequest request) {
    Sort sort = Sort.by(Sort.Direction.fromString(request.getSortDirection()), request.getSortBy());

    return userBehaviorRepository.filter(request.getUserId(), request.getProductId(), sort).stream()
        .map(userBehaviorMapper::toUserBehaviorResponse)
        .collect(Collectors.toList());
  }

  @Cacheable("user_behaviors_paging")
  public Page<UserBehaviorResponse> getAllUserBehaviorsWithPaging(
      UserBehaviorFilterRequest request) {
    Sort sort = Sort.by(Sort.Direction.fromString(request.getSortDirection()), request.getSortBy());

    Pageable pageable = PageRequest.of(request.getPage() - 1, request.getPageSize(), sort);

    return userBehaviorRepository
        .filterWithPaging(request.getUserId(), request.getProductId(), pageable)
        .map(userBehaviorMapper::toUserBehaviorResponse);
  }
}
