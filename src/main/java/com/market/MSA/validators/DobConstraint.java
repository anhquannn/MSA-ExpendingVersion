package com.market.MSA.validators;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target({FIELD})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = {DobValidator.class})
public @interface DobConstraint {
  String message() default "Invalid date of birth";

  int min();

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
