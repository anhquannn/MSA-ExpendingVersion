package com.market.MSA.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<PasswordConstraint, String> {
  private int min;

  @Override
  public void initialize(PasswordConstraint constraintAnnotation) {
    this.min = constraintAnnotation.min();
  }

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    if (value == null) {
      return true; // để @NotBlank xử lý null
    }
    return value.trim().length() >= min;
  }
}
