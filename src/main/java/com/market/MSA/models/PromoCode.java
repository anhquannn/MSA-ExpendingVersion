package com.market.MSA.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
@Table(name = "promocodes")
public class PromoCode {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  long promoCodeId;

  String name;
  String code;
  String description;
  Date startDate;
  Date endDate;
  String status;
  String discountType;
  double discountPercentage;
  double minimumOrderValue;
}
