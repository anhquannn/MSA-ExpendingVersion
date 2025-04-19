package com.market.MSA.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = RatingConstraintValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RatingConstraint {
  String message() default "Rating must be between 1 and 5";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
