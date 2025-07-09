package com.market.MSA.models.order;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.market.MSA.constants.OrderStatus;
import com.market.MSA.models.others.DeliveryInfo;
import com.market.MSA.models.others.Notification;
import com.market.MSA.models.others.Payment;
import com.market.MSA.models.product.Branch;
import com.market.MSA.models.user.RewardPointTransaction;
import com.market.MSA.models.user.User;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.PositiveOrZero;
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
@Table(
    name = "orders",
    indexes = {
      @Index(name = "idx_order_user", columnList = "user_id"),
      @Index(name = "idx_order_branch", columnList = "branch_id"),
      @Index(name = "idx_order_date", columnList = "order_date"),
      @Index(name = "idx_order_status", columnList = "status")
    })
public class Order {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long orderId;

  LocalDateTime orderDate;

  @PositiveOrZero
  double grandTotal;

  @Enumerated(EnumType.STRING)
  OrderStatus status;

  @ManyToOne
  @JoinColumn(
      name = "branch_id",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_order_branch"))
  @JsonBackReference("order-branches")
  Branch branch;

  @ManyToOne
  @JoinColumn(name = "cart_id", nullable = false, foreignKey = @ForeignKey(name = "fk_order_cart"))
  @JsonBackReference("order-carts")
  Cart cart;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_order_user"))
  @JsonBackReference("user-orders")
  User user;

  @OneToOne(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonManagedReference("order-delivery-info")
  DeliveryInfo deliveryInfo;

  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonManagedReference("order-cancel-orders")
  List<CancelOrder> cancelOrders = new ArrayList<>();

  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonManagedReference("order-payments")
  List<Payment> payments = new ArrayList<>();

  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonManagedReference("order-details")
  List<OrderDetail> orderDetails = new ArrayList<>();

  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonManagedReference("order-notifications")
  List<Notification> notifications = new ArrayList<>();

  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonManagedReference("order-reward-transactions")
  List<RewardPointTransaction> rewardPointTransactions = new ArrayList<>();

  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonManagedReference("order-promo-usages")
  List<PromoCodeUsage> promoCodeUsages = new ArrayList<>();

  @ManyToMany
  @JoinTable(
      name = "order_promocodes",
      joinColumns = @JoinColumn(name = "order_id"),
      inverseJoinColumns = @JoinColumn(name = "promo_code_id"))
  @JsonManagedReference("promoCode-orders")
  List<PromoCode> promoCodes = new ArrayList<>();
}
