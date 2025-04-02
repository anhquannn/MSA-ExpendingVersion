package com.market.MSA.controllers;

import com.market.MSA.requests.PermissionRequest;
import com.market.MSA.responses.ApiResponse;
import com.market.MSA.responses.PermissionResponse;
import com.market.MSA.services.PermissionService;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/permission")
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PermissionController {
  PermissionService permissionService;

  @PostMapping
  ApiResponse<PermissionResponse> createPermission(@RequestBody PermissionRequest request) {

    return ApiResponse.<PermissionResponse>builder()
        .result(permissionService.createPermission(request))
        .build();
  }

  @GetMapping
  ApiResponse<List<PermissionResponse>> getAll() {
    return ApiResponse.<List<PermissionResponse>>builder()
        .result(permissionService.getAll())
        .build();
  }

  @DeleteMapping("/{permissionId}")
  ApiResponse<Boolean> deletePermission(@PathVariable long permissionId) {
    Boolean result = permissionService.delete(permissionId);
    return ApiResponse.<Boolean>builder().result(result).build();
  }

  @PutMapping("/{id}")
  public ApiResponse<PermissionResponse> updatePermission(
      @PathVariable Long id, @RequestBody PermissionRequest request) {
    return ApiResponse.<PermissionResponse>builder()
        .result(permissionService.updatePermission(id, request))
        .build();
  }
}
