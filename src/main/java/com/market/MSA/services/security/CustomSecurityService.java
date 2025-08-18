package com.market.MSA.services.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * Custom security service for flexible role-based authorization Provides methods for pattern-based
 * role checking
 */
@Service("customSecurity")
public class CustomSecurityService {

  /**
   * Check if current user has ADMIN role
   *
   * @return true if user has ADMIN role
   */
  public boolean isAdmin() {
    return hasRole("ADMIN");
  }

  /**
   * Check if current user has any MANAGER role (MANAGER_1, MANAGER_2, etc.)
   *
   * @return true if user has any role starting with "MANAGER"
   */
  public boolean isManager() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !authentication.isAuthenticated()) {
      return false;
    }

    return authentication.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .anyMatch(authority -> authority.startsWith("MANAGER"));
  }

  /**
   * Check if current user has ADMIN role OR any MANAGER role
   *
   * @return true if user has ADMIN or any MANAGER role
   */
  public boolean isAdminOrManager() {
    return isAdmin() || isManager();
  }

  /**
   * Check if current user has a specific role
   *
   * @param role the role to check
   * @return true if user has the specified role
   */
  public boolean hasRole(String role) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !authentication.isAuthenticated()) {
      return false;
    }

    return authentication.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .anyMatch(authority -> authority.equals(role));
  }

  /**
   * Check if current user has any role matching the pattern
   *
   * @param pattern the pattern to match (e.g., "MANAGER" for roles starting with MANAGER)
   * @return true if user has any role matching the pattern
   */
  public boolean hasRolePattern(String pattern) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !authentication.isAuthenticated()) {
      return false;
    }

    return authentication.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .anyMatch(authority -> authority.startsWith(pattern));
  }

  /**
   * Check if current user has CUSTOMER role
   *
   * @return true if user has CUSTOMER role
   */
  public boolean isCustomer() {
    return hasRole("CUSTOMER");
  }

  /**
   * Check if current user has SURVEYOR role
   *
   * @return true if user has SURVEYOR role
   */
  public boolean isSurveyor() {
    return hasRole("SURVEYOR");
  }
}
