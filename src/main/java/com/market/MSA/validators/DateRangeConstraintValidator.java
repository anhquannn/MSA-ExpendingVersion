package com.market.MSA.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateRangeConstraintValidator
    implements ConstraintValidator<DateRangeConstraint, Object> {
  private String startDateField;
  private String endDateField;
  private SimpleDateFormat dateFormat;

  @Override
  public void initialize(DateRangeConstraint constraintAnnotation) {
    this.startDateField = constraintAnnotation.startDate();
    this.endDateField = constraintAnnotation.endDate();
    this.dateFormat = new SimpleDateFormat("yyyy-MM-dd");
  }

  @Override
  public boolean isValid(Object object, ConstraintValidatorContext context) {
    try {
      Field startDate = object.getClass().getDeclaredField(startDateField);
      Field endDate = object.getClass().getDeclaredField(endDateField);

      startDate.setAccessible(true);
      endDate.setAccessible(true);

      String startDateValue = (String) startDate.get(object);
      String endDateValue = (String) endDate.get(object);

      if (startDateValue == null || endDateValue == null) {
        return true;
      }

      Date start = dateFormat.parse(startDateValue);
      Date end = dateFormat.parse(endDateValue);

      return !end.before(start);
    } catch (NoSuchFieldException | IllegalAccessException | ParseException e) {
      return false;
    }
  }
}
