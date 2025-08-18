package com.market.MSA.configurations;

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
      BranchRepository branchRepository,
      InventoryRepository inventoryRepository) {
    return args -> {
      // Create FULL_ACCESS permission if not exists
      Permission fullAccessPermission =
          permissionRepository
              .findByName("FULL_ACCESS")
              .orElseGet(
                  () ->
                      permissionRepository.save(
                          Permission.builder()
                              .name("FULL_ACCESS")
                              .description("Toàn quyền")
                              .build()));

      // Create ADMIN role with FULL_ACCESS permission if not exists
      Role adminRole =
          roleRepository
              .findByName("ADMIN")
              .orElseGet(
                  () -> {
                    Role role =
                        Role.builder().name("ADMIN").description("Quản trị viên hệ thống").build();
                    role.setPermissions(Set.of(fullAccessPermission));
                    return roleRepository.save(role);
                  });

      // Create default branch if not exists
      Branch defaultBranch =
          branchRepository
              .findByName("HEAD")
              .orElseGet(
                  () -> {
                    // Create branch first without saving
                    Branch branch =
                        Branch.builder()
                            .name("HEAD")
                            .phone("0123456789")
                            .street("Default Street")
                            .ward("Default Ward")
                            .district("Default District")
                            .city("Default City")
                            .cityCode("700000")
                            .wardCode("9218")
                            .districtCode("700400")
                            .build();

                    // Create inventory
                    Inventory inventory =
                        Inventory.builder()
                            .name("Kho chính")
                            .address(
                                branch.getStreet()
                                    + ", "
                                    + branch.getWard()
                                    + ", "
                                    + branch.getDistrict()
                                    + ", "
                                    + branch.getCity())
                            .contact(branch.getPhone())
                            .totalRevenue(0)
                            .build();

                    // Set the bidirectional relationship
                    inventory.setBranch(branch);
                    branch.setInventory(inventory);

                    // Save the branch (will cascade to inventory due to CascadeType.ALL)
                    branch = branchRepository.save(branch);
                    return branch;
                  });

      // Create CUSTOMER permission if not exists
      Permission customerPermission =
          permissionRepository
              .findByName("CUSTOMER_ACCESS")
              .orElseGet(
                  () ->
                      permissionRepository.save(
                          Permission.builder()
                              .name("CUSTOMER_ACCESS")
                              .description("Truy cập dành cho khách hàng")
                              .build()));

      // Create CUSTOMER role with permission if not exists
      Role customerRole =
          roleRepository
              .findByName("CUSTOMER")
              .orElseGet(
                  () -> {
                    Role role = Role.builder().name("CUSTOMER").description("Khách hàng").build();
                    role.setPermissions(Set.of(customerPermission));
                    return roleRepository.save(role);
                  });

      // Create admin user if not exists
      if (userRepository.findByEmail("admin@example.com").isEmpty()) {
        User adminUser =
            User.builder()
                .email("admin@example.com")
                .password(passwordEncoder.encode("admin123"))
                .fullName("Admin")
                .phoneNumber("0123456789")
                .branches(new ArrayList<>(List.of(defaultBranch)))
                .build();

        adminUser.setRoles(Set.of(adminRole));

        // Save the admin user
        adminUser = userRepository.save(adminUser);

        // Update the branch with the admin user
        if (defaultBranch.getUsers() == null) {
          defaultBranch.setUsers(new ArrayList<>());
        }
        defaultBranch.getUsers().add(adminUser);
        branchRepository.save(defaultBranch);
      }

      // Create SURVEYOR permission if not exists
      Permission surveyorPermission =
          permissionRepository
              .findByName("SURVEYOR_ACCESS")
              .orElseGet(
                  () ->
                      permissionRepository.save(
                          Permission.builder()
                              .name("SURVEYOR_ACCESS")
                              .description("Truy cập dành cho nhân viên khảo sát")
                              .build()));

      // Create SURVEYOR role with permission if not exists
      Role surveyorRole =
          roleRepository
              .findByName("SURVEYOR")
              .orElseGet(
                  () -> {
                    Role role =
                        Role.builder().name("SURVEYOR").description("Nhân viên khảo sát").build();
                    role.setPermissions(Set.of(surveyorPermission));
                    return roleRepository.save(role);
                  });

      // Create surveyor user if not exists
      if (userRepository.findByEmail("surveyor@example.com").isEmpty()) {
        User surveyorUser =
            User.builder()
                .email("surveyor@example.com")
                .password(passwordEncoder.encode("surveyor123"))
                .fullName("Surveyor")
                .phoneNumber("0987654321")
                .build();

        surveyorUser.setRoles(Set.of(surveyorRole));

        // Save the surveyor user
        userRepository.save(surveyorUser);
      }
    };
  }
}
