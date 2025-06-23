package com.market.MSA.mappers.user;

import com.market.MSA.models.user.UserAddress;
import com.market.MSA.models.user.UserBehavior;
import com.market.MSA.requests.user.UserAddressRequest;
import com.market.MSA.requests.user.UserBehaviorRequest;
import com.market.MSA.responses.user.UserAddressResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
@Component
public interface UserAddressMappper {
    UserAddress toUserAddress(UserAddressRequest request);

    @Mapping(target = "user.userAddresses", ignore = true)
    UserAddressResponse toUserAddressResponse(UserAddress userAddress);

    @Mapping(target = "userAddressId", ignore = true)
    void updateUserAddressFromRequest(
            UserAddressRequest request, @MappingTarget UserAddress userAddress);
}
