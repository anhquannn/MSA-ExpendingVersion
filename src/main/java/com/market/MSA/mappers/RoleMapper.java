package com.market.MSA.mappers;

import com.market.MSA.models.Role;
import com.market.MSA.requests.RoleRequest;
import com.market.MSA.responses.RoleResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface RoleMapper {
  @Mapping(target = "permissions", ignore = true)
  Role toRole(RoleRequest request);

  RoleResponse toRoleResponse(Role role);

  @Mapping(target = "roleId", ignore = true)
  @Mapping(target = "permissions", ignore = true)
  void updateRoleFromRequest(RoleRequest request, @MappingTarget Role role);
}
