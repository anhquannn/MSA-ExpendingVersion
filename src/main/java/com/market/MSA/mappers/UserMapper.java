package com.market.MSA.mappers;

import com.market.MSA.models.User;
import com.market.MSA.requests.UpdateUserRequest;
import com.market.MSA.requests.UserRequest;
import com.market.MSA.responses.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
  User toUser(UserRequest request);

  UserResponse toUserResponse(User user);

  @Mapping(target = "roles", ignore = true)
  void updateUser(@MappingTarget User user, UpdateUserRequest request);
}
