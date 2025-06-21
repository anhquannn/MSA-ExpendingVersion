package com.market.MSA.services.user;

import com.market.MSA.exceptions.AppException;
import com.market.MSA.exceptions.ErrorCode;
import com.market.MSA.mappers.user.PermissionMapper;
import com.market.MSA.models.user.Permission;
import com.market.MSA.repositories.user.PermissionRepository;
import com.market.MSA.requests.user.PermissionRequest;
import com.market.MSA.responses.user.PermissionResponse;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class PermissionService {
  final PermissionRepository permissionRepository;
  final PermissionMapper permissionMapper;

  @Transactional
  public PermissionResponse createPermission(PermissionRequest request) {
    Permission permission = permissionMapper.toPermission(request);
    permission = permissionRepository.save(permission);

    return permissionMapper.toPermissionResponse(permission);
  }

  @Transactional
  public PermissionResponse updatePermission(Long id, PermissionRequest request) {
    Permission permission =
        permissionRepository
            .findById(id)
            .orElseThrow(() -> new AppException(ErrorCode.PERMISSION_NOT_FOUND));

    permissionMapper.updatePermissionFromRequest(request, permission);

    Permission updatedPermission = permissionRepository.save(permission);
    return permissionMapper.toPermissionResponse(updatedPermission);
  }

  public List<PermissionResponse> getAll() {
    var permissions = permissionRepository.findAll();
    return permissions.stream().map(permissionMapper::toPermissionResponse).toList();
  }

  @Transactional
  public boolean delete(long id) {
    if (!permissionRepository.existsById(id)) {
      throw new AppException(ErrorCode.PERMISSION_NOT_FOUND);
    }
    permissionRepository.deleteById(id);
    return true;
  }
}
