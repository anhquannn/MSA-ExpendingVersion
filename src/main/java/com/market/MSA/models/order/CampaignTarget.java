package com.market.MSA.models.order;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.market.MSA.constants.PromoScopeType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "campaign_targets")
public class CampaignTarget {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  @Enumerated(EnumType.STRING)
  PromoScopeType targetType;
  Long targetId;

  @ManyToOne
  @JoinColumn(name = "campaignId", nullable = false)
  @JsonBackReference("campaign-targets")
  Campaign campaign;
}
