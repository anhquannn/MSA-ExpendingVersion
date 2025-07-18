package com.market.MSA.validators;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Target({FIELD})
@Retention(RUNTIME)
@Constraint(validatedBy = PasswordValidator.class)
public @interface PasswordConstraint {
  String message() default "Mật khẩu phải có ít nhất {min} ký tự";

  int min() default 6;

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
