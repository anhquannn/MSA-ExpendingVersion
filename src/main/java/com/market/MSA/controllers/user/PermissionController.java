package com.market.MSA.controllers.user;

import com.market.MSA.constants.ApiMessage;
import com.market.MSA.requests.user.PermissionRequest;
import com.market.MSA.responses.others.ApiResponse;
import com.market.MSA.responses.user.PermissionResponse;
import com.market.MSA.services.user.PermissionService;
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
        .message(ApiMessage.PERMISSION_CREATED.getMessage())
        .build();
  }

  @GetMapping
  ApiResponse<List<PermissionResponse>> getAll() {
    return ApiResponse.<List<PermissionResponse>>builder()
        .result(permissionService.getAll())
        .message(ApiMessage.ALL_PERMISSIONS_RETRIEVED.getMessage())
        .build();
  }

  @DeleteMapping("/{permissionId}")
  ApiResponse<Boolean> deletePermission(@PathVariable long permissionId) {
    Boolean result = permissionService.delete(permissionId);
    return ApiResponse.<Boolean>builder()
        .result(result)
        .message(ApiMessage.PERMISSION_DELETED.getMessage())
        .build();
  }

  @PutMapping("/{id}")
  public ApiResponse<PermissionResponse> updatePermission(
      @PathVariable Long id, @RequestBody PermissionRequest request) {
    return ApiResponse.<PermissionResponse>builder()
        .result(permissionService.updatePermission(id, request))
        .message(ApiMessage.PERMISSION_UPDATED.getMessage())
        .build();
  }
}
