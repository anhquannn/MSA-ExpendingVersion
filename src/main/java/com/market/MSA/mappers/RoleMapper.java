package com.market.MSA.mappers;

import com.market.MSA.models.Role;
import com.market.MSA.requests.RoleRequest;
import com.market.MSA.responses.RoleResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoleMapper {
  @Mapping(target = "permissions", ignore = true)
  Role toRole(RoleRequest request);

  RoleResponse toRoleResponse(Role role);
}
