package com.market.MSA.models.others;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.market.MSA.constants.OrderStatus;
import com.market.MSA.models.order.Order;
import jakarta.persistence.*;
import java.time.LocalDateTime;
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
  String cityCode;
  String districtCode;
  String wardCode;
  String cod;
  String weight;
  String width;
  String height;
  String length;
  String metadata;

  @Enumerated(EnumType.STRING)
  OrderStatus status;

  LocalDateTime deliveryDate;

  @OneToOne
  @JoinColumn(
      name = "order_id",
      nullable = false,
      unique = true,
      foreignKey = @ForeignKey(name = "fk_delivery_info_order"))
  @JsonBackReference("order-delivery-info")
  Order order;
}
