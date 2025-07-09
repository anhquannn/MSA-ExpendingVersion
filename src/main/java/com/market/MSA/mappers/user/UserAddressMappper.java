package com.market.MSA.mappers.user;

import com.market.MSA.models.user.UserAddress;
import com.market.MSA.requests.user.UserAddressRequest;
import com.market.MSA.responses.user.UserAddressResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.stereotype.Component;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
@Component
public interface UserAddressMappper {
  UserAddress toUserAddress(UserAddressRequest request);

  UserAddressResponse toUserAddressResponse(UserAddress userAddress);

  @Mapping(target = "userAddressId", ignore = true)
  void updateUserAddressFromRequest(
      UserAddressRequest request, @MappingTarget UserAddress userAddress);
}
