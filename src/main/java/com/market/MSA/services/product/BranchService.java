package com.market.MSA.services.product;

import com.market.MSA.exceptions.AppException;
import com.market.MSA.exceptions.ErrorCode;
import com.market.MSA.mappers.product.BranchMapper;
import com.market.MSA.models.product.Branch;
import com.market.MSA.repositories.product.BranchRepository;
import com.market.MSA.requests.product.BranchRequest;
import com.market.MSA.responses.product.BranchResponse;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BranchService {
  final BranchRepository branchRepository;

  final BranchMapper branchMapper;

  final ProductService productService;

  // Create Branch
  public BranchResponse createBranch(BranchRequest branchRequest) {
    Branch branch = branchMapper.toBranch(branchRequest);
    branch = branchRepository.save(branch);
    return branchMapper.toBranchResponse(branch);
  }

  // Update Branch
  public BranchResponse updateBranch(Long branchId, BranchRequest branchRequest) {
    Optional<Branch> optionalBranch = branchRepository.findById(branchId);
    if (optionalBranch.isPresent()) {
      Branch branch = optionalBranch.get();
      branchMapper.updateBranchFromRequest(branchRequest, branch);
      branch = branchRepository.save(branch);
      return branchMapper.toBranchResponse(branch);
    } else {
      throw new AppException(ErrorCode.BRANCH_NOT_FOUND);
    }
  }

  // Delete Branch
  public boolean deleteBranch(Long branchId) {
    Optional<Branch> optionalBranch = branchRepository.findById(branchId);
    if (optionalBranch.isPresent()) {
      branchRepository.deleteById(branchId);
      return true;
    } else {
      throw new AppException(ErrorCode.BRANCH_NOT_FOUND);
    }
  }

  // Get Branch by ID
  public BranchResponse getBranchById(Long branchId) {
    Optional<Branch> optionalBranch = branchRepository.findById(branchId);
    if (optionalBranch.isPresent()) {
      return branchMapper.toBranchResponse(optionalBranch.get());
    } else {
      throw new AppException(ErrorCode.BRANCH_NOT_FOUND);
    }
  }

  public BranchResponse getBranchByRole(String role) {
    Branch optionalBranch = branchRepository.findByUserRole(role);
    return branchMapper.toBranchResponse(optionalBranch);
  }

  public List<BranchResponse> getBranchesByProductId(Long productId) {
    // Kiểm tra sản phẩm có tồn tại không
    productService.findProductById(productId);

    // Lấy danh sách chi nhánh có sản phẩm này
    List<Branch> branches = branchRepository.findByProductId(productId);

    // Chuyển đổi sang response
    return branches.stream().map(branchMapper::toBranchResponse).collect(Collectors.toList());
  }

  public Page<BranchResponse> getAllBranches(
      int page, int size, String sortBy, String sortDirection) {
    // Create pageable with sorting
    Sort.Direction direction = Sort.Direction.fromString(sortDirection.toUpperCase());
    Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

    // Get all branches with pagination
    Page<Branch> branches = branchRepository.findAll(pageable);

    // Convert to response DTOs
    return branches.map(branchMapper::toBranchResponse);
  }
}
