package com.market.MSA.controllers.user;

import com.market.MSA.constants.ApiMessage;
import com.market.MSA.requests.filters.UserAddressFilterRequest;
import com.market.MSA.requests.user.UserAddressRequest;
import com.market.MSA.responses.others.ApiResponse;
import com.market.MSA.responses.user.UserAddressResponse;
import com.market.MSA.services.user.UserAddressService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/address")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserAddressController {
  UserAddressService userAddressService;

  @PostMapping
  public ApiResponse<UserAddressResponse> createUserAddress(
      @Valid @RequestBody UserAddressRequest request) {
    return ApiResponse.<UserAddressResponse>builder()
        .result(userAddressService.createUserAddress(request))
        .message(ApiMessage.ADDRESS_CREATED.getMessage())
        .build();
  }

  @PutMapping("/{addressId}")
  public ApiResponse<UserAddressResponse> updateUserAddress(
      @PathVariable Long addressId, @Valid @RequestBody UserAddressRequest request) {
    return ApiResponse.<UserAddressResponse>builder()
        .result(userAddressService.updateUserAddress(addressId, request))
        .message(ApiMessage.ADDRESS_UPDATED.getMessage())
        .build();
  }

  @DeleteMapping("/{addressId}")
  public ApiResponse<Boolean> deleteUserAddress(@PathVariable Long addressId) {
    userAddressService.deleteUserAddress(addressId);
    return ApiResponse.<Boolean>builder()
        .result(true)
        .message(ApiMessage.ADDRESS_DELETED.getMessage())
        .build();
  }

  @GetMapping("/{addressId}")
  public ApiResponse<UserAddressResponse> getUserAddressById(@PathVariable Long addressId) {
    return ApiResponse.<UserAddressResponse>builder()
        .result(userAddressService.getUserAddressById(addressId))
        .message(ApiMessage.ADDRESS_RETRIEVED.getMessage())
        .build();
  }

  @GetMapping
  public ApiResponse<List<UserAddressResponse>> getAll() {
    return ApiResponse.<List<UserAddressResponse>>builder()
        .result(userAddressService.getAll())
        .message(ApiMessage.ALL_ADDRESSES_RETRIEVED.getMessage())
        .build();
  }

  @PostMapping("/list")
  public ApiResponse<List<UserAddressResponse>> getAllUserAddress(
      @RequestBody @Valid UserAddressFilterRequest request) {
    return ApiResponse.<List<UserAddressResponse>>builder()
        .result(userAddressService.getAllUserAddresses(request))
        .message(ApiMessage.ALL_ADDRESSES_RETRIEVED.getMessage())
        .build();
  }

  @PostMapping("/paging")
  public ApiResponse<Page<UserAddressResponse>> getAllUserAddressWithPaging(
      @RequestBody @Valid UserAddressFilterRequest request) {
    return ApiResponse.<Page<UserAddressResponse>>builder()
        .result(userAddressService.getAllUserAddressesWithPaging(request))
        .message(ApiMessage.ALL_ADDRESSES_RETRIEVED.getMessage())
        .build();
  }
}
