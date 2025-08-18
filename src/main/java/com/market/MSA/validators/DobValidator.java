package com.market.MSA.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class DobValidator implements ConstraintValidator<DobConstraint, LocalDateTime> {
  private int min;

  @Override
  public boolean isValid(LocalDateTime value, ConstraintValidatorContext context) {
    if (Objects.isNull(value)) {
      return true;
    }

    long years = ChronoUnit.YEARS.between(value, LocalDateTime.now());

    return years >= min;
  }

  @Override
  public void initialize(DobConstraint constraintAnnotation) {
    ConstraintValidator.super.initialize(constraintAnnotation);
    min = constraintAnnotation.min();
  }
}
