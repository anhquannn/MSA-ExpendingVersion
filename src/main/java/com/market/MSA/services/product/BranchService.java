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
import com.market.MSA.requests.filters.BranchFilterRequest;
import com.market.MSA.requests.product.BranchRequest;
import com.market.MSA.requests.product.CreateBranchWithManagerRequest;
import com.market.MSA.responses.product.BranchResponse;
import java.util.*;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
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
  public BranchResponse createBranch(BranchRequest branchRequest) {
    Branch branch = branchMapper.toBranch(branchRequest);
    branch = branchRepository.save(branch);
    return branchMapper.toBranchResponse(branch);
  }

  @Transactional
  public BranchResponse createBranchWithManager(CreateBranchWithManagerRequest request) {
    // 1. Tạo thực thể (entity) Chi nhánh (Branch)
    // Sử dụng Builder pattern để tạo đối tượng Branch từ request.
    Branch branch =
        Branch.builder()
            .name(request.getBranchName())
            .phone(request.getBranchPhone())
            .street(request.getBranchStreet())
            .ward(request.getBranchWard())
            .district(request.getBranchDistrict())
            .city(request.getBranchCity())
            .cityCode(request.getBranchCityCode())
            .wardCode(request.getBranchWardCode())
            .districtCode(request.getBranchDistrictCode())
            .build();
    // Lưu chi nhánh vào cơ sở dữ liệu (CSDL) để lấy ID.
    branch = branchRepository.save(branch);

    // 2. Tạo kho (Inventory) cho chi nhánh vừa tạo.
    // Mỗi chi nhánh sẽ có một kho tương ứng để quản lý tồn kho sản phẩm.
    Inventory inventory =
        Inventory.builder()
            .branch(branch) // Liên kết kho với chi nhánh
            .totalRevenue(0)
            .name(request.getInventoryName())
            .address(request.getInventoryAddress())
            .contact(request.getInventoryContact())
            .build();
    inventoryRepository.save(inventory);

    // 3. Tạo quyền (Permission) đặc thù để quản lý chi nhánh này.
    // Tên quyền được tạo duy nhất dựa trên ID của chi nhánh (ví dụ: "MANAGE_BRANCH_123").
    String permissionName = "MANAGE_BRANCH_" + branch.getBranchId();
    String permissionDesc = "Quản lý chi nhánh " + branch.getBranchId();

    Permission permission =
        Permission.builder().name(permissionName).description(permissionDesc).build();
    permission = permissionRepository.save(permission);

    // 4. Tạo vai trò (Role) quản lý cho chi nhánh.
    // Tên vai trò cũng được tạo duy nhất (ví dụ: "MANAGER_123").
    String roleName = "MANAGER_" + branch.getBranchId();
    Role role =
        Role.builder()
            .name(roleName)
            .description("Quản lý chi nhánh " + branch.getBranchId())
            .build();

    // Gán quyền vừa tạo vào vai trò này.
    Set<Permission> permissions = new HashSet<>();
    permissions.add(permission);
    role.setPermissions(permissions);
    role = roleRepository.save(role);

    // 5. Tạo người dùng (User) với vai trò quản lý và gán vào chi nhánh.
    // Kiểm tra xem email của quản lý đã tồn tại trong hệ thống chưa.
    if (userRepository.findByEmail(request.getManagerEmail()).isPresent()) {
      throw new AppException(ErrorCode.USER_EXISTED);
    }

    // Tạo đối tượng User và mã hóa mật khẩu.
    User user =
        User.builder()
            .email(request.getManagerEmail())
            .password(passwordEncoder.encode(request.getManagerPassword()))
            .phoneNumber(request.getManagerPhoneNumber())
            .fullName(request.getManagerFullName())
            .build();

    // Khởi tạo danh sách chi nhánh nếu nó là null để tránh NullPointerException.
    if (user.getBranches() == null) {
      user.setBranches(new ArrayList<>());
    }
    // Thêm chi nhánh này vào danh sách các chi nhánh mà người dùng quản lý.
    user.getBranches().add(branch);

    // Gán vai trò quản lý vừa tạo cho người dùng.
    Set<Role> roles = new HashSet<>();
    roles.add(role);
    user.setRoles(roles);

    // Lưu người dùng (thao tác này cũng sẽ lưu mối quan hệ user-branch).
    user = userRepository.save(user);

    // 6. Thiết lập mối quan hệ hai chiều: thêm người dùng vào danh sách của chi nhánh.
    if (branch.getUsers() == null) {
      branch.setUsers(new ArrayList<>());
    }
    branch.getUsers().add(user);
    branchRepository.save(branch);

    // Trả về thông tin chi nhánh đã tạo.
    return branchMapper.toBranchResponse(branch);
  }

  // Update Branch
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

  @Cacheable(value = "branches_list")
  public List<BranchResponse> getAllBranches(BranchFilterRequest request) {
    return branchRepository
        .filter(request.getKeyword(), request.getProductId(), request.getUserId())
        .stream()
        .map(branchMapper::toBranchResponse)
        .collect(Collectors.toList());
  }

  @Cacheable(value = "all_branches")
  public List<BranchResponse> getAll() {
    return branchRepository.findAll().stream()
        .map(branchMapper::toBranchResponse)
        .collect(Collectors.toList());
  }

  @Cacheable("branches_paging")
  public Page<BranchResponse> getAllBranchesWithPaging(BranchFilterRequest request) {
    Sort sort = Sort.by(Sort.Direction.fromString(request.getSortDirection()), request.getSortBy());

    Pageable pageable = PageRequest.of(request.getPage() - 1, request.getPageSize(), sort);

    return branchRepository
        .filterWithPaging(
            request.getKeyword(), request.getProductId(), request.getUserId(), pageable)
        .map(branchMapper::toBranchResponse);
  }
}
