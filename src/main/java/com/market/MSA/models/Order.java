package com.market.MSA.models;

import jakarta.persistence.*;
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
@Table(name = "orders")
public class Order {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  long orderId;

  Date orderDate;
  double grandTotal;
  String status;

  @ManyToOne
  @JoinColumn(name = "branchId", nullable = false)
  Branch branch;

  @ManyToOne
  @JoinColumn(name = "cartId", nullable = false)
  Cart cart;

  @ManyToOne
  @JoinColumn(name = "userId", nullable = false)
  User user;

  @OneToOne(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
  DeliveryInfo deliveryInfo;

  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
  List<ReturnOrder> returnOrders;

  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
  List<Payment> payments;

  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
  List<OrderDetail> orderDetails;

  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
  List<Notification> notifications;

  @ManyToMany List<PromoCode> promoCodes;
}
