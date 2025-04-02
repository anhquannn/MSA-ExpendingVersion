package com.market.MSA.controllers;

import com.market.MSA.requests.RoleRequest;
import com.market.MSA.responses.ApiResponse;
import com.market.MSA.responses.RoleResponse;
import com.market.MSA.services.RoleService;
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

    return ApiResponse.<RoleResponse>builder().result(roleService.createRole(request)).build();
  }

  @GetMapping
  ApiResponse<List<RoleResponse>> getAll() {
    return ApiResponse.<List<RoleResponse>>builder().result(roleService.getAll()).build();
  }

  @DeleteMapping("/{roleId}")
  ApiResponse<Boolean> delete(@PathVariable long roleId) {
    Boolean result = roleService.delete(roleId);

    return ApiResponse.<Boolean>builder().result(result).build();
  }

  @PutMapping("/{id}")
  public ApiResponse<RoleResponse> updateRole(
      @PathVariable Long id, @RequestBody RoleRequest request) {
    return ApiResponse.<RoleResponse>builder().result(roleService.updateRole(id, request)).build();
  }
}
