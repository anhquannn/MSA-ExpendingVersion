package com.market.MSA.models;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.Date;
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
@Table(name = "deliveryinfos")
public class DeliveryInfo {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  long deliveryInfoId;

  Date deliveryDate;
  String status;

  @ManyToOne
  @JoinColumn(name = "userId", nullable = false)
  User user;

  @ManyToOne
  @JoinColumn(name = "orderId", nullable = false)
  Order order;

  @OneToMany(mappedBy = "deliveryInfo", cascade = CascadeType.ALL, orphanRemoval = true)
  List<DeliveryDetail> deliveryDetails;
}
