package com.market.MSA.models.others;

import com.market.MSA.models.order.Order;
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
@Table(name = "deliveryInfos")
public class DeliveryInfo {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long deliveryInfoId;

  String street;
  String ward;
  String district;
  String city;
  String cod;
  String weight;
  String width;
  String height;
  String length;
  String metadata;
  String status;
  Date deliveryDate;

  @OneToOne
  @JoinColumn(name = "orderId", nullable = false, unique = true)
  Order order;
}
