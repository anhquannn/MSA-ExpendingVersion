package com.market.MSA.mappers.user;

import com.market.MSA.models.user.Role;
import com.market.MSA.models.user.User;
import com.market.MSA.requests.user.UpdateUserRequest;
import com.market.MSA.requests.user.UserRequest;
import com.market.MSA.responses.user.UserResponse;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
@Component
public interface UserMapper {
  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mapping(target = "branches", ignore = true)
  User toUser(UserRequest request);

  @Mapping(target = "branches", ignore = true)
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
