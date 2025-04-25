package com.market.MSA.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = CampaignDateRangeConstraintValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface CampaignDateRangeConstraint {
  String message() default "PromoCode dates must be within Campaign date range";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
