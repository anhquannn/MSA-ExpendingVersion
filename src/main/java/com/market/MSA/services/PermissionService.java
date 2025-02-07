package com.market.MSA.services;

import com.market.MSA.mappers.PermissionMapper;
import com.market.MSA.models.Permission;
import com.market.MSA.repositories.PermissionRepository;
import com.market.MSA.requests.PermissionRequest;
import com.market.MSA.responses.PermissionResponse;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class PermissionService {
  final PermissionRepository permissionRepository;
  final PermissionMapper permissionMapper;

  public PermissionResponse createPermission(PermissionRequest request) {
    Permission permission = permissionMapper.toPermission(request);
    permission = permissionRepository.save(permission);

    return permissionMapper.toPermissionResponse(permission);
  }

  public List<PermissionResponse> getAll() {
    var permissions = permissionRepository.findAll();
    return permissions.stream().map(permissionMapper::toPermissionResponse).toList();
  }

  public void delete(long id) {
    permissionRepository.deleteById(id);
  }
}
