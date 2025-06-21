package com.market.MSA.requests.branch;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateBranchWithManagerRequest {
  // Branch fields
  @NotBlank(message = "Branch name is required")
  private String branchName;

  @NotBlank(message = "Branch phone is required")
  private String branchPhone;

  @NotBlank(message = "Branch street is required")
  private String branchStreet;

  @NotBlank(message = "Branch ward is required")
  private String branchWard;

  @NotBlank(message = "Branch district is required")
  private String branchDistrict;

  @NotBlank(message = "Branch city is required")
  private String branchCity;

  // Inventory fields
  @NotBlank(message = "Inventory name is required")
  private String inventoryName;

  @NotBlank(message = "Inventory address is required")
  private String inventoryAddress;

  @NotBlank(message = "Inventory contact is required")
  private String inventoryContact;

  // Manager user fields
  @NotBlank(message = "Manager full name is required")
  private String managerFullName;

  @NotBlank(message = "Email is required")
  @Email(message = "Email should be valid")
  private String managerEmail;

  @NotBlank(message = "Phone number is required")
  private String managerPhoneNumber;

  @NotBlank(message = "Password is required")
  @Size(min = 6, message = "Password must be at least 6 characters")
  private String managerPassword;
}
