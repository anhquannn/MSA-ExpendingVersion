package com.market.MSA.configurations;

import com.market.MSA.exceptions.AppException;
import com.market.MSA.exceptions.ErrorCode;
import com.market.MSA.models.Branch;
import com.market.MSA.models.Permission;
import com.market.MSA.models.Role;
import com.market.MSA.models.User;
import com.market.MSA.repositories.BranchRepository;
import com.market.MSA.repositories.PermissionRepository;
import com.market.MSA.repositories.RoleRepository;
import com.market.MSA.repositories.UserRepository;
import java.util.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ApplicationinitConfig {
  PasswordEncoder passwordEncoder;

  @Bean
  ApplicationRunner applicationRunner(
      UserRepository userRepository,
      RoleRepository roleRepository,
      PermissionRepository permissionRepository,
      BranchRepository branchRepository) {
    return args -> {
      Map<String, String> permissions =
          Map.of(
              "MANAGE_BRANCH_1", "Quản lý chi nhánh 1",
              "MANAGE_BRANCH_2", "Quản lý chi nhánh 2",
              "MANAGE_BRANCH_3", "Quản lý chi nhánh 3",
              "MANAGE_INVENTORY_1", "Quản lý kho 1",
              "MANAGE_INVENTORY_2", "Quản lý kho 2",
              "MANAGE_INVENTORY_3", "Quản lý kho 3",
              "FULL_ACCESS", "Toàn quyền");

      Map<String, Permission> createdPermissions = new HashMap<>();

      for (var entry : permissions.entrySet()) {
        createdPermissions.put(
            entry.getKey(),
            permissionRepository
                .findByName(entry.getKey())
                .orElseGet(
                    () ->
                        permissionRepository.save(
                            Permission.builder()
                                .name(entry.getKey())
                                .description(entry.getValue())
                                .build())));
      }

      Map<String, Set<Permission>> rolesWithPermissions = new HashMap<>();
      rolesWithPermissions.put("ADMIN", new HashSet<>(createdPermissions.values()));
      rolesWithPermissions.put(
          "MANAGER_1",
          Set.of(
              createdPermissions.get("MANAGE_BRANCH_1"),
              createdPermissions.get("MANAGE_INVENTORY_1")));
      rolesWithPermissions.put(
          "MANAGER_2",
          Set.of(
              createdPermissions.get("MANAGE_BRANCH_2"),
              createdPermissions.get("MANAGE_INVENTORY_2")));
      rolesWithPermissions.put(
          "MANAGER_3",
          Set.of(
              createdPermissions.get("MANAGE_BRANCH_3"),
              createdPermissions.get("MANAGE_INVENTORY_3")));
      rolesWithPermissions.put("CUSTOMER", Collections.emptySet());

      for (var entry : rolesWithPermissions.entrySet()) {
        roleRepository
            .findByName(entry.getKey())
            .orElseGet(
                () ->
                    roleRepository.save(
                        Role.builder().name(entry.getKey()).permissions(entry.getValue()).build()));
      }

      var adminRole =
          roleRepository
              .findById((long) 1)
              .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));

      if (userRepository.findByEmail("nguyenanhquan20102003@gmail.com").isEmpty()
          || branchRepository.findByName("").isEmpty()) {
        Branch branch =
            Branch.builder()
                .name("")
                .address("")
                .city("")
                .state("")
                .zip("")
                .country("")
                .email("")
                .contact("")
                .build();

        branchRepository.save(branch);

        User user =
            User.builder()
                .email("nguyenanhquan20102003@gmail.com")
                .fullName("admin")
                .password(passwordEncoder.encode("admin"))
                .roles(Set.of(adminRole))
                .branch(branch)
                .build();

        userRepository.save(user);
        log.warn("admin user has been created!");
      }
    };
  }
}
