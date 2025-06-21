package com.market.MSA.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = StockNumberConstraintValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface StockNumberConstraint {
  String message() default "Stock number cannot be negative";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
