package com.market.MSA.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = DateRangeConstraintValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface DateRangeConstraint {
  String message() default "End date must be after start date";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  String startDate();

  String endDate();
}
