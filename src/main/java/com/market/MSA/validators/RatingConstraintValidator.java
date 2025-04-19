package com.market.MSA.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class RatingConstraintValidator implements ConstraintValidator<RatingConstraint, Integer> {
  @Override
  public boolean isValid(Integer rating, ConstraintValidatorContext context) {
    if (rating == null) {
      return true;
    }
    return rating >= 1 && rating <= 5;
  }
}
