package com.market.MSA.services.product;

import com.market.MSA.exceptions.AppException;
import com.market.MSA.exceptions.ErrorCode;
import com.market.MSA.mappers.product.BranchMapper;
import com.market.MSA.models.product.Branch;
import com.market.MSA.models.product.Inventory;
import com.market.MSA.models.user.Permission;
import com.market.MSA.models.user.Role;
import com.market.MSA.models.user.User;
import com.market.MSA.repositories.product.BranchRepository;
import com.market.MSA.repositories.product.InventoryRepository;
import com.market.MSA.repositories.user.PermissionRepository;
import com.market.MSA.repositories.user.RoleRepository;
import com.market.MSA.repositories.user.UserRepository;
import com.market.MSA.requests.branch.CreateBranchWithManagerRequest;
import com.market.MSA.requests.filters.BranchFilterRequest;
import com.market.MSA.requests.product.BranchRequest;
import com.market.MSA.responses.product.BranchResponse;
import java.util.*;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BranchService {
  final BranchRepository branchRepository;
  final InventoryRepository inventoryRepository;
  final PermissionRepository permissionRepository;
  final RoleRepository roleRepository;
  final UserRepository userRepository;
  final PasswordEncoder passwordEncoder;
  final BranchMapper branchMapper;

  // Create Branch
//  @CacheEvict(
//      value = {"branches", "branch", "branch_entity"},
//      allEntries = true)
  public BranchResponse createBranch(BranchRequest branchRequest) {
    Branch branch = branchMapper.toBranch(branchRequest);
    branch = branchRepository.save(branch);
    return branchMapper.toBranchResponse(branch);
  }

//  @CacheEvict(
//      value = {"branches", "branch", "branch_entity"},
//      allEntries = true)
  @Transactional
  public BranchResponse createBranchWithManager(CreateBranchWithManagerRequest request) {
    // 1. Create the branch
    Branch branch =
        Branch.builder()
            .name(request.getBranchName())
            .phone(request.getBranchPhone())
            .street(request.getBranchStreet())
            .ward(request.getBranchWard())
            .district(request.getBranchDistrict())
            .city(request.getBranchCity())
            .build();
    branch = branchRepository.save(branch);

    // 2. Create inventory for the branch
    Inventory inventory =
        Inventory.builder()
            .branch(branch)
            .totalRevenue(0)
            .name(request.getInventoryName())
            .address(request.getInventoryAddress())
            .contact(request.getInventoryContact())
            .build();
    inventory = inventoryRepository.save(inventory);

    // 3. Create permission for managing this branch
    String permissionName = "MANAGE_BRANCH_" + branch.getBranchId();
    String permissionDesc = "Quản lý chi nhánh " + branch.getBranchId();

    Permission permission =
        Permission.builder().name(permissionName).description(permissionDesc).build();
    permission = permissionRepository.save(permission);

    // 4. Create role for branch manager
    String roleName = "MANAGER_" + branch.getBranchId();
    Role role =
        Role.builder()
            .name(roleName)
            .description("Quản lý chi nhánh " + branch.getBranchId())
            .build();

    // Add the permission to the role
    Set<Permission> permissions = new HashSet<>();
    permissions.add(permission);
    role.setPermissions(permissions);

    role = roleRepository.save(role);

    // 5. Create manager user and assign the role
    if (userRepository.findByEmail(request.getManagerEmail()).isPresent()) {
      throw new AppException(ErrorCode.USER_EXISTED);
    }

    // Create user with branch relationship
    User user =
        User.builder()
            .email(request.getManagerEmail())
            .password(passwordEncoder.encode(request.getManagerPassword()))
            .phoneNumber(request.getManagerPhoneNumber())
            .fullName(request.getManagerFullName())
            .build();

    // Initialize the branches set if it's null
    if (user.getBranches() == null) {
      user.setBranches(new ArrayList<>());
    }

    // Add the branch to user's branches
    user.getBranches().add(branch);

    // Assign the manager role
    Set<Role> roles = new HashSet<>();
    roles.add(role);
    user.setRoles(roles);

    // Save the user (this will also save the user_branches relationship)
    user = userRepository.save(user);

    // Ensure the relationship is properly set on both sides
    if (branch.getUsers() == null) {
      branch.setUsers(new ArrayList<>());
    }
    branch.getUsers().add(user);
    branchRepository.save(branch);

    return branchMapper.toBranchResponse(branch);
  }

  // Update Branch
//  @Caching(
//      evict = {
//        @CacheEvict(value = "branch", key = "#branchId"),
//        @CacheEvict(value = "branch_entity", key = "#branchId"),
//        @CacheEvict(value = "branches", allEntries = true)
//      })
  @Transactional
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
//  @Caching(
//      evict = {
//        @CacheEvict(value = "branch", key = "#branchId"),
//        @CacheEvict(value = "branch_entity", key = "#branchId"),
//        @CacheEvict(value = "branches", allEntries = true)
//      })
  @Transactional
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
//  @Cacheable(value = "branch", key = "#branchId", unless = "#result == null")
  public BranchResponse getBranchById(Long branchId) {
    log.info("Fetching branch from database with id: {}", branchId);
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

//  @Cacheable("branches")
  public List<BranchResponse> getAllBranches(BranchFilterRequest request) {
    Sort sort = Sort.by(Sort.Direction.fromString(request.getSortDirection()), request.getSortBy());

    return branchRepository.filter(request.getKeyword(), request.getProductId()).stream()
        .map(branchMapper::toBranchResponse)
        .collect(Collectors.toList());
  }

//  @Cacheable("branches")
  public Page<BranchResponse> getAllBranchesWithPaging(BranchFilterRequest request) {
    Sort sort = Sort.by(Sort.Direction.fromString(request.getSortDirection()), request.getSortBy());

    Pageable pageable = PageRequest.of(request.getPage() - 1, request.getPageSize(), sort);

    return branchRepository
        .filterWithPaging(request.getKeyword(), request.getProductId(), pageable)
        .map(branchMapper::toBranchResponse);
  }
}
