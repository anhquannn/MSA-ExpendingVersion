package com.market.MSA.services;

import com.market.MSA.exceptions.AppException;
import com.market.MSA.exceptions.ErrorCode;
import com.market.MSA.mappers.RoleMapper;
import com.market.MSA.models.Role;
import com.market.MSA.repositories.PermissionRepository;
import com.market.MSA.repositories.RoleRepository;
import com.market.MSA.requests.RoleRequest;
import com.market.MSA.responses.RoleResponse;
import java.util.HashSet;
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
public class RoleService {
  final RoleRepository roleRepository;
  final PermissionRepository permissionRepository;
  final RoleMapper roleMapper;

  @Transactional
  public RoleResponse createRole(RoleRequest request) {
    var role = roleMapper.toRole(request);

    var permissions = permissionRepository.findAllById(request.getPermissions());
    role.setPermissions(new HashSet<>(permissions));
    role = roleRepository.save(role);
    return roleMapper.toRoleResponse(role);
  }

  public List<RoleResponse> getAll() {
    var roles = roleRepository.findAll();
    return roles.stream().map(roleMapper::toRoleResponse).toList();
  }

  public RoleResponse updateRole(long id, RoleRequest request) {
    Role role =
        roleRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));

    roleMapper.updateRoleFromRequest(request, role);

    Role updatedRole = roleRepository.save(role);

    return roleMapper.toRoleResponse(updatedRole);
  }

  @Transactional
  public void delete(long id) {
    roleRepository.deleteById(id);
  }
}
