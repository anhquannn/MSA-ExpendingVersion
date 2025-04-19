package com.market.MSA.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class StockNumberConstraintValidator
    implements ConstraintValidator<StockNumberConstraint, Integer> {
  @Override
  public boolean isValid(Integer stockNumber, ConstraintValidatorContext context) {
    if (stockNumber == null) {
      return true;
    }
    return stockNumber >= 0;
  }
}
