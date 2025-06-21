package com.market.MSA.mappers.user;

import com.market.MSA.models.user.Role;
import com.market.MSA.requests.user.RoleRequest;
import com.market.MSA.responses.user.RoleResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
@Component
public interface RoleMapper {
  @Mapping(target = "permissions", ignore = true)
  Role toRole(RoleRequest request);

  RoleResponse toRoleResponse(Role role);

  @Mapping(target = "roleId", ignore = true)
  @Mapping(target = "permissions", ignore = true)
  void updateRoleFromRequest(RoleRequest request, @MappingTarget Role role);
}
