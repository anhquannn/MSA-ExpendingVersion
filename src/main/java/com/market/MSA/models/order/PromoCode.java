package com.market.MSA.models.order;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.market.MSA.constants.PromocodeStatus;
import com.market.MSA.validators.CampaignDateRangeConstraint;
import com.market.MSA.validators.DateRangeConstraint;
import com.market.MSA.validators.DiscountPercentageConstraint;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
  LocalDateTime startDate;
  LocalDateTime endDate;

  @Enumerated(EnumType.STRING)
  PromocodeStatus status;

  @DiscountPercentageConstraint double discountPercentage;

  @ManyToOne
  @JoinColumn(
      name = "campaign_id",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_promo_code_campaign"))
  @JsonBackReference("campaign-promoCodes")
  Campaign campaign;

  @OneToMany(mappedBy = "promoCode", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonManagedReference("promoCode-usages")
  List<PromoCodeUsage> promoCodeUsages = new ArrayList<>();

  @ManyToMany(mappedBy = "promoCodes")
  @JsonBackReference("promoCode-orders")
  List<Order> orders = new ArrayList<>();
}
