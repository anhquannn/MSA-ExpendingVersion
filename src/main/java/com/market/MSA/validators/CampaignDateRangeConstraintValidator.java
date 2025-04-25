package com.market.MSA.validators;

import com.market.MSA.models.order.PromoCode;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CampaignDateRangeConstraintValidator
    implements ConstraintValidator<CampaignDateRangeConstraint, PromoCode> {

  @Override
  public boolean isValid(PromoCode promoCode, ConstraintValidatorContext context) {
    if (promoCode == null
        || promoCode.getCampaign() == null
        || promoCode.getStartDate() == null
        || promoCode.getEndDate() == null
        || promoCode.getCampaign().getStartDate() == null
        || promoCode.getCampaign().getEndDate() == null) {
      return true;
    }

    // Kiểm tra startDate của PromoCode phải sau hoặc bằng startDate của Campaign
    boolean isStartDateValid =
        !promoCode.getStartDate().before(promoCode.getCampaign().getStartDate());

    // Kiểm tra endDate của PromoCode phải trước hoặc bằng endDate của Campaign
    boolean isEndDateValid = !promoCode.getEndDate().after(promoCode.getCampaign().getEndDate());

    return isStartDateValid && isEndDateValid;
  }
}
