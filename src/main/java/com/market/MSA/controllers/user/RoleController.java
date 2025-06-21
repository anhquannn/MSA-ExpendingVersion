package com.market.MSA.controllers.user;

import com.market.MSA.constants.ApiMessage;
import com.market.MSA.requests.user.RoleRequest;
import com.market.MSA.responses.others.ApiResponse;
import com.market.MSA.responses.user.RoleResponse;
import com.market.MSA.services.user.RoleService;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/role")
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleController {
  RoleService roleService;

  @PostMapping
  ApiResponse<RoleResponse> createRole(@RequestBody RoleRequest request) {
    return ApiResponse.<RoleResponse>builder()
        .result(roleService.createRole(request))
        .message(ApiMessage.ROLE_CREATED.getMessage())
        .build();
  }

  @GetMapping
  ApiResponse<List<RoleResponse>> getAll() {
    return ApiResponse.<List<RoleResponse>>builder()
        .result(roleService.getAll())
        .message(ApiMessage.ALL_ROLES_RETRIEVED.getMessage())
        .build();
  }

  @DeleteMapping("/{roleId}")
  ApiResponse<Boolean> delete(@PathVariable long roleId) {
    Boolean result = roleService.delete(roleId);
    return ApiResponse.<Boolean>builder()
        .result(result)
        .message(ApiMessage.ROLE_DELETED.getMessage())
        .build();
  }

  @PutMapping("/{id}")
  public ApiResponse<RoleResponse> updateRole(
      @PathVariable Long id, @RequestBody RoleRequest request) {
    return ApiResponse.<RoleResponse>builder()
        .result(roleService.updateRole(id, request))
        .message(ApiMessage.ROLE_UPDATED.getMessage())
        .build();
  }
}
