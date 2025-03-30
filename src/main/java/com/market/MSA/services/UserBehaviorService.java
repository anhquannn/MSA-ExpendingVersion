package com.market.MSA.services;

import com.market.MSA.exceptions.AppException;
import com.market.MSA.exceptions.ErrorCode;
import com.market.MSA.mappers.UserBehaviorMapper;
import com.market.MSA.models.UserBehavior;
import com.market.MSA.repositories.UserBehaviorRepository;
import com.market.MSA.requests.UserBehaviorRequest;
import com.market.MSA.responses.UserBehaviorResponse;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserBehaviorService {

  UserBehaviorRepository userBehaviorRepository;
  UserBehaviorMapper userBehaviorMapper;

  // Tạo UserBehavior
  @Transactional
  public UserBehaviorResponse createUserBehavior(UserBehaviorRequest request) {
    UserBehavior userBehavior = userBehaviorMapper.toUserBehavior(request);
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

    userBehaviorMapper.updateUserBehaviorFromRequest(request, userBehavior);
    UserBehavior updatedUserBehavior = userBehaviorRepository.save(userBehavior);
    return userBehaviorMapper.toUserBehaviorResponse(updatedUserBehavior);
  }

  // Xóa UserBehavior
  @Transactional
  public void deleteUserBehavior(Long id) {
    if (!userBehaviorRepository.existsById(id)) {
      throw new AppException(ErrorCode.USER_BEHAVIOR_NOT_FOUND);
    }
    userBehaviorRepository.deleteById(id);
  }

  // Lấy UserBehavior theo ID
  public UserBehaviorResponse getUserBehaviorById(Long id) {
    UserBehavior userBehavior =
        userBehaviorRepository
            .findById(id)
            .orElseThrow(() -> new AppException(ErrorCode.USER_BEHAVIOR_NOT_FOUND));
    return userBehaviorMapper.toUserBehaviorResponse(userBehavior);
  }

  // Lấy tất cả UserBehavior (phân trang)
  public List<UserBehaviorResponse> getAllUserBehaviors(int page, int pageSize) {
    Pageable pageable = PageRequest.of(page - 1, pageSize); // Page bắt đầu từ 0
    return userBehaviorRepository.findAll(pageable).stream()
        .map(userBehaviorMapper::toUserBehaviorResponse)
        .collect(Collectors.toList());
  }

  // Lấy UserBehavior theo userId (phân trang)
  public List<UserBehaviorResponse> getUserBehaviorsByUserId(Long userId, int page, int pageSize) {
    Pageable pageable = PageRequest.of(page - 1, pageSize);
    List<UserBehavior> userBehaviors = userBehaviorRepository.findByUserId(userId, pageable);
    return userBehaviors.stream()
        .map(userBehaviorMapper::toUserBehaviorResponse)
        .collect(Collectors.toList());
  }
}
