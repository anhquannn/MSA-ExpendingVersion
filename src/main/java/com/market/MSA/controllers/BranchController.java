package com.market.MSA.controllers;

import com.market.MSA.requests.BranchRequest;
import com.market.MSA.responses.ApiResponse;
import com.market.MSA.responses.BranchResponse;
import com.market.MSA.services.BranchService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/branch")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BranchController {
  BranchService branchService;

  // Create Branch
  @PostMapping
  public ApiResponse<BranchResponse> createBranch(@RequestBody BranchRequest branchRequest) {
    BranchResponse branchResponse = branchService.createBranch(branchRequest);
    return ApiResponse.<BranchResponse>builder().result(branchResponse).build();
  }

  // Update Branch
  @PutMapping("/{branchId}")
  public ApiResponse<BranchResponse> updateBranch(
      @PathVariable Long branchId, @RequestBody BranchRequest branchRequest) {
    BranchResponse branchResponse = branchService.updateBranch(branchId, branchRequest);
    return ApiResponse.<BranchResponse>builder().result(branchResponse).build();
  }

  // Delete Branch
  @DeleteMapping("/{branchId}")
  public ApiResponse<Void> deleteBranch(@PathVariable Long branchId) {
    branchService.deleteBranch(branchId);
    return ApiResponse.<Void>builder().result(null).build();
  }

  // Get Branch by ID
  @GetMapping("/{branchId}")
  public ApiResponse<BranchResponse> getBranchById(@PathVariable Long branchId) {
    BranchResponse branchResponse = branchService.getBranchById(branchId);
    return ApiResponse.<BranchResponse>builder().result(branchResponse).build();
  }
}
