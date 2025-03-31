package com.market.MSA.mappers;

import com.market.MSA.models.Role;
import com.market.MSA.models.User;
import com.market.MSA.requests.UpdateUserRequest;
import com.market.MSA.requests.UserRequest;
import com.market.MSA.responses.UserResponse;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface UserMapper {
  User toUser(UserRequest request);

  UserResponse toUserResponse(User user);

  @Mapping(target = "roles", source = "roles", qualifiedByName = "mapRoles")
  @Mapping(target = "userId", ignore = true)
  void updateUser(@MappingTarget User user, UpdateUserRequest request);

  @Named("mapRoles")
  default Set<Role> mapRoles(List<Long> roleIds) {
    if (roleIds == null || roleIds.isEmpty()) {
      return new HashSet<>();
    }
    return roleIds.stream()
        .map(
            id -> {
              Role role = new Role();
              role.setRoleId(id);
              return role;
            })
        .collect(Collectors.toSet());
  }
}
