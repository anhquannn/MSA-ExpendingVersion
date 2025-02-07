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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/permissions")
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

  @DeleteMapping("/{permission}")
  ApiResponse<Void> delete(@PathVariable long id) {
    permissionService.delete(id);

    return ApiResponse.<Void>builder().build();
  }
}
