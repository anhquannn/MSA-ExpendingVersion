package com.market.MSA.models.order;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.market.MSA.constants.PromoScopeType;
import com.market.MSA.constants.PromocodeStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(
    name = "campaigns",
    indexes = {
      @Index(name = "idx_campaign", columnList = "name"),
      @Index(name = "idx_campaign_dates", columnList = "start_date, end_date")
    })
public class Campaign {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long campaignId;

  String name;
  String description;

  @Enumerated(EnumType.STRING)
  PromocodeStatus status;

  LocalDateTime startDate;
  LocalDateTime endDate;

  @Enumerated(EnumType.STRING)
  PromoScopeType scopeType = PromoScopeType.ALL;

  @PositiveOrZero double minOrderValue;

  @OneToMany(mappedBy = "campaign", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonManagedReference("campaign-targets")
  List<CampaignTarget> targets = new ArrayList<>();

  @OneToMany(mappedBy = "campaign", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonManagedReference("campaign-promoCodes")
  List<PromoCode> promoCodes = new ArrayList<>();
}
