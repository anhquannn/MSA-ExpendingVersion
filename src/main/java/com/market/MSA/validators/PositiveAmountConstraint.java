package com.market.MSA.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PositiveAmountConstraintValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface PositiveAmountConstraint {
  String message() default "Amount must be greater than 0";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
