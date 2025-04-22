package com.market.MSA.controllers.product;

import com.market.MSA.constants.ApiMessage;
import com.market.MSA.requests.product.BranchRequest;
import com.market.MSA.responses.others.ApiResponse;
import com.market.MSA.responses.product.BranchResponse;
import com.market.MSA.services.product.BranchService;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    return ApiResponse.<BranchResponse>builder()
        .result(branchResponse)
        .message(ApiMessage.BRANCH_CREATED.getMessage())
        .build();
  }

  // Update Branch
  @PutMapping("/{branchId}")
  public ApiResponse<BranchResponse> updateBranch(
      @PathVariable Long branchId, @RequestBody BranchRequest branchRequest) {
    BranchResponse branchResponse = branchService.updateBranch(branchId, branchRequest);
    return ApiResponse.<BranchResponse>builder()
        .result(branchResponse)
        .message(ApiMessage.BRANCH_UPDATED.getMessage())
        .build();
  }

  // Delete Branch
  @DeleteMapping("/{branchId}")
  public ApiResponse<Boolean> deleteBranch(@PathVariable Long branchId) {
    boolean result = branchService.deleteBranch(branchId);
    return ApiResponse.<Boolean>builder()
        .result(result)
        .message(ApiMessage.BRANCH_DELETED.getMessage())
        .build();
  }

  // Get Branch by ID
  @GetMapping("/{branchId}")
  public ApiResponse<BranchResponse> getBranchById(@PathVariable Long branchId) {
    BranchResponse branchResponse = branchService.getBranchById(branchId);
    return ApiResponse.<BranchResponse>builder()
        .result(branchResponse)
        .message(ApiMessage.BRANCH_RETRIEVED.getMessage())
        .build();
  }

  @GetMapping("/role/{roleName}")
  public ApiResponse<BranchResponse> getBranchByRole(@PathVariable String roleName) {
    BranchResponse branchResponse = branchService.getBranchByRole(roleName);
    return ApiResponse.<BranchResponse>builder()
        .result(branchResponse)
        .message(ApiMessage.BRANCH_RETRIEVED.getMessage())
        .build();
  }

  @GetMapping("/product/{productId}")
  public ApiResponse<List<BranchResponse>> getBranchesByProductId(@PathVariable Long productId) {
    return ApiResponse.<List<BranchResponse>>builder()
        .result(branchService.getBranchesByProductId(productId))
        .message(ApiMessage.ALL_BRANCHES_RETRIEVED.getMessage())
        .build();
  }

  @GetMapping
  public ApiResponse<Page<BranchResponse>> getAllBranches(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(defaultValue = "branchId") String sortBy,
      @RequestParam(defaultValue = "asc") String sortDirection) {
    return ApiResponse.<Page<BranchResponse>>builder()
        .result(branchService.getAllBranches(page, size, sortBy, sortDirection))
        .message(ApiMessage.ALL_BRANCHES_RETRIEVED.getMessage())
        .build();
  }
}
