package com.market.MSA.models.order;

import com.market.MSA.models.others.DeliveryInfo;
import com.market.MSA.models.others.Notification;
import com.market.MSA.models.others.Payment;
import com.market.MSA.models.product.Branch;
import com.market.MSA.models.user.RewardPointTransaction;
import com.market.MSA.models.user.User;
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
  List<CancelOrder> cancelOrders;

  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
  List<Payment> payments;

  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
  List<OrderDetail> orderDetails;

  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
  List<Notification> notifications;

  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
  List<RewardPointTransaction> rewardPointTransactions;

  @ManyToMany List<PromoCode> promoCodes;
}
