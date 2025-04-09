package com.market.MSA.models;

import jakarta.persistence.*;
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
@Table(name = "deliveryInfos")
public class DeliveryInfo {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  long deliveryInfoId;

  String street1;
  String city;
  String state;
  String zip;
  String country;
  String weight;
  String status;

  @OneToOne
  @JoinColumn(name = "orderId", nullable = false, unique = true)
  Order order;
}
