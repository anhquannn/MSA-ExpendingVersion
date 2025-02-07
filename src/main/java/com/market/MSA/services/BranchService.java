package com.market.MSA.services;

import com.market.MSA.exceptions.AppException;
import com.market.MSA.exceptions.ErrorCode;
import com.market.MSA.mappers.BranchMapper;
import com.market.MSA.models.Branch;
import com.market.MSA.repositories.BranchRepository;
import com.market.MSA.requests.BranchRequest;
import com.market.MSA.responses.BranchResponse;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BranchService {
  BranchRepository branchRepository;

  BranchMapper branchMapper;

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
  public void deleteBranch(Long branchId) {
    Optional<Branch> optionalBranch = branchRepository.findById(branchId);
    if (optionalBranch.isPresent()) {
      branchRepository.deleteById(branchId);
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
}
