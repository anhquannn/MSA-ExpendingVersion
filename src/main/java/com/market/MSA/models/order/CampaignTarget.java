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
@Table(
    name = "campaign_targets",
    indexes = {@Index(name = "idx_campaign_target", columnList = "targetType")})
public class CampaignTarget {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long campaignTargetId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  PromoScopeType targetType;

  Long targetId;

  @ManyToOne
  @JoinColumn(
      name = "campaign_id",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_campaign_target_campaign"))
  @JsonBackReference("campaign-targets")
  Campaign campaign;
}
