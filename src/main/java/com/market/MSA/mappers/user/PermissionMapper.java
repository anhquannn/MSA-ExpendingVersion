package com.market.MSA.mappers.user;

import com.market.MSA.models.user.Permission;
import com.market.MSA.requests.user.PermissionRequest;
import com.market.MSA.responses.user.PermissionResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.stereotype.Component;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
@Component
public interface PermissionMapper {
  Permission toPermission(PermissionRequest request);

  PermissionResponse toPermissionResponse(Permission permission);

  @Mapping(target = "permissionId", ignore = true)
  void updatePermissionFromRequest(PermissionRequest request, @MappingTarget Permission permission);
}
