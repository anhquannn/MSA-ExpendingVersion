package com.market.MSA.models.order;

import com.market.MSA.validators.CampaignDateRangeConstraint;
import com.market.MSA.validators.DateRangeConstraint;
import com.market.MSA.validators.DiscountPercentageConstraint;
import com.market.MSA.validators.PositiveAmountConstraint;
import jakarta.persistence.*;
import java.util.Date;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(
    name = "promo_codes",
    indexes = {
      @Index(name = "idx_promo_code", columnList = "code", unique = true),
      @Index(name = "idx_promo_dates", columnList = "start_date,end_date")
    })
@DateRangeConstraint(startDate = "startDate", endDate = "endDate")
@CampaignDateRangeConstraint
public class PromoCode {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long promoCodeId;

  String name;
  String code;
  String description;
  Date startDate;
  Date endDate;
  String status;
  String discountType;

  @DiscountPercentageConstraint double discountPercentage;

  @PositiveAmountConstraint double minimumOrderValue;

  @ManyToOne
  @JoinColumn(name = "campaignId", nullable = false)
  Campaign campaign;
}
