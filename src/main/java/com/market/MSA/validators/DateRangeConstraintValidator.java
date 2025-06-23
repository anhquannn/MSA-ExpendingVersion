package com.market.MSA.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;
import java.time.LocalDateTime;

public class DateRangeConstraintValidator
    implements ConstraintValidator<DateRangeConstraint, Object> {
  private String startDateField;
  private String endDateField;

  @Override
  public void initialize(DateRangeConstraint constraintAnnotation) {
    this.startDateField = constraintAnnotation.startDate();
    this.endDateField = constraintAnnotation.endDate();
  }

  @Override
  public boolean isValid(Object object, ConstraintValidatorContext context) {
    try {
      if (object == null) {
        return true;
      }

      Class<?> objectClass = object.getClass();
      Field startDateField = objectClass.getDeclaredField(this.startDateField);
      Field endDateField = objectClass.getDeclaredField(this.endDateField);

      startDateField.setAccessible(true);
      endDateField.setAccessible(true);

      LocalDateTime startDate = (LocalDateTime) startDateField.get(object);
      LocalDateTime endDate = (LocalDateTime) endDateField.get(object);

      // If either date is null, we can't validate the range
      if (startDate == null || endDate == null) {
        return true;
      }

      // Check if end date is not before start date
      return !endDate.isBefore(startDate);
    } catch (NoSuchFieldException | IllegalAccessException e) {
      // Log the error if needed
      return false;
    } catch (ClassCastException e) {
      // This will be thrown if the fields are not LocalDateTime
      throw new IllegalArgumentException("Date fields must be of type LocalDateTime", e);
    }
  }
}
