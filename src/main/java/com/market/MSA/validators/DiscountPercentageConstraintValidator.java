package com.market.MSA.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DiscountPercentageConstraintValidator
    implements ConstraintValidator<DiscountPercentageConstraint, Double> {
  @Override
  public boolean isValid(Double percentage, ConstraintValidatorContext context) {
    if (percentage == null) {
      return true;
    }
    return percentage >= 0 && percentage <= 100;
  }
}
