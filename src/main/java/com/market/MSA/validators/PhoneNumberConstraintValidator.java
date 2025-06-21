package com.market.MSA.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class PhoneNumberConstraintValidator
    implements ConstraintValidator<PhoneNumberConstraint, String> {
  private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?[0-9]{10,15}$");

  @Override
  public boolean isValid(String phoneNumber, ConstraintValidatorContext context) {
    if (phoneNumber == null) {
      return true;
    }
    return PHONE_PATTERN.matcher(phoneNumber).matches();
  }
}
