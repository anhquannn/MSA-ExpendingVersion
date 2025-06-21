package com.market.MSA.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PositiveAmountConstraintValidator
    implements ConstraintValidator<PositiveAmountConstraint, Double> {
  @Override
  public boolean isValid(Double amount, ConstraintValidatorContext context) {
    if (amount == null) {
      return true;
    }
    return amount > 0;
  }
}
