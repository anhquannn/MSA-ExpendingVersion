package com.market.MSA.mappers;

import com.market.MSA.models.Permission;
import com.market.MSA.requests.PermissionRequest;
import com.market.MSA.responses.PermissionResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
  Permission toPermission(PermissionRequest request);

  PermissionResponse toPermissionResponse(Permission permission);

  @Mapping(target = "permissionId", ignore = true)
  void updatePermissionFromRequest(PermissionRequest request, @MappingTarget Permission permission);
}
