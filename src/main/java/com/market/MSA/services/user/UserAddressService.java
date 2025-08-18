package com.market.MSA.services.user;

import com.market.MSA.exceptions.AppException;
import com.market.MSA.exceptions.ErrorCode;
import com.market.MSA.mappers.user.UserAddressMappper;
import com.market.MSA.models.user.UserAddress;
import com.market.MSA.repositories.user.UserAddressRepository;
import com.market.MSA.repositories.user.UserRepository;
import com.market.MSA.requests.filters.UserAddressFilterRequest;
import com.market.MSA.requests.user.UserAddressRequest;
import com.market.MSA.responses.user.UserAddressResponse;
import com.market.MSA.services.others.EntityFinderService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class UserAddressService {
  final UserAddressRepository userAddressRepository;
  final UserAddressMappper userAddressMappper;
  private final EntityFinderService entityFinderService;
  private final UserRepository userRepository;

  @Transactional
  public UserAddressResponse createUserAddress(UserAddressRequest request) {
    UserAddress address = userAddressMappper.toUserAddress(request);
    address.setUser(
        entityFinderService.findByIdOrThrow(
            userRepository, request.getUserId(), ErrorCode.USER_NOT_EXISTED));
    address.setCreatedAt(LocalDateTime.now());
    address = userAddressRepository.save(address);
    return userAddressMappper.toUserAddressResponse(address);
  }

  @Transactional
  public UserAddressResponse updateUserAddress(Long addressId, UserAddressRequest request) {
    UserAddress address =
        userAddressRepository
            .findById(addressId)
            .orElseThrow(() -> new AppException(ErrorCode.ADDRESS_NOT_FOUND));
    address.setUser(
        entityFinderService.findByIdOrThrow(
            userRepository, request.getUserId(), ErrorCode.USER_NOT_EXISTED));
    userAddressMappper.updateUserAddressFromRequest(request, address);

    UserAddress updatedAddress = userAddressRepository.save(address);
    return userAddressMappper.toUserAddressResponse(updatedAddress);
  }

  @Transactional
  public Boolean deleteUserAddress(Long addressId) {
    if (!userAddressRepository.existsById(addressId)) {
      throw new AppException(ErrorCode.ADDRESS_NOT_FOUND);
    }
    userAddressRepository.deleteById(addressId);
    return true;
  }

  @Transactional
  public UserAddressResponse getUserAddressById(Long addressId) {
    UserAddress userAddress =
        userAddressRepository
            .findById(addressId)
            .orElseThrow(() -> new AppException(ErrorCode.ADDRESS_NOT_FOUND));
    return userAddressMappper.toUserAddressResponse(userAddress);
  }

  @Cacheable("all_user_addresses")
  @Transactional(readOnly = true)
  public List<UserAddressResponse> getAll() {
    return userAddressRepository.findAll().stream()
        .map(userAddressMappper::toUserAddressResponse)
        .collect(Collectors.toList());
  }

  @Cacheable("user_addresses_list")
  @Transactional(readOnly = true)
  public List<UserAddressResponse> getAllUserAddresses(UserAddressFilterRequest request) {
    Sort sort = Sort.by(Sort.Direction.fromString(request.getSortDirection()), request.getSortBy());
    return userAddressRepository.filter(request.getUserId(), sort).stream()
        .map(userAddressMappper::toUserAddressResponse)
        .collect(Collectors.toList());
  }

  @Cacheable("user_addresses_paging")
  @Transactional(readOnly = true)
  public Page<UserAddressResponse> getAllUserAddressesWithPaging(UserAddressFilterRequest request) {
    Sort sort = Sort.by(Sort.Direction.fromString(request.getSortDirection()), request.getSortBy());

    PageRequest pageable = PageRequest.of(request.getPage() - 1, request.getPageSize(), sort);
    return userAddressRepository
        .filterWithPaging(request.getUserId(), pageable)
        .map(userAddressMappper::toUserAddressResponse);
  }
}
