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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/roles")
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

  @DeleteMapping("/{role}")
  ApiResponse<Void> delete(@PathVariable long id) {
    roleService.delete(id);

    return ApiResponse.<Void>builder().build();
  }
}
