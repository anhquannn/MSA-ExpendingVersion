package com.market.MSA.models.order;

import jakarta.persistence.*;
import java.util.Date;
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
  String status;
  Date startDate;
  Date endDate;

  @OneToMany(mappedBy = "campaign", cascade = CascadeType.ALL, orphanRemoval = true)
  List<PromoCode> promoCodes;
}
