package com.market.MSA.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = DiscountPercentageConstraintValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DiscountPercentageConstraint {
  String message() default "Discount percentage must be between 0 and 100";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
