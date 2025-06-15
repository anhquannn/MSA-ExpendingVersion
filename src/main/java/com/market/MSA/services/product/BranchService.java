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
import com.market.MSA.requests.product.BranchRequest;
import com.market.MSA.responses.product.BranchResponse;
import java.util.*;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
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
  final ProductService productService;

  // Create Branch
  public BranchResponse createBranch(BranchRequest branchRequest) {
    Branch branch = branchMapper.toBranch(branchRequest);
    branch = branchRepository.save(branch);
    return branchMapper.toBranchResponse(branch);
  }

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
