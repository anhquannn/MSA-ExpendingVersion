package com.market.MSA.models.others;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.market.MSA.constants.OrderStatus;
import com.market.MSA.models.order.Order;
import com.market.MSA.models.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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
@Table(
    name = "payments",
    indexes = {
      @Index(name = "idx_payment_user", columnList = "user_id"),
      @Index(name = "idx_payment_order", columnList = "order_id"),
      @Index(name = "idx_payment_date", columnList = "paymentDate"),
      @Index(name = "idx_payment_method", columnList = "paymentMethod"),
      @Index(name = "idx_payment_status", columnList = "status")
    })
public class Payment {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long paymentId;

  String paymentMethod;
  String paymentDate;

  @Enumerated(EnumType.STRING)
  OrderStatus status;

  double grandTotal;
  String transactionId;

  String bankCode;
  String bankTranNo;
  String responseCode;
  LocalDateTime updateDate;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "userId", nullable = false, foreignKey = @ForeignKey(name = "fk_payment_user"))
  @JsonBackReference("user-payments")
  @NotNull(message = "User is required")
  User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "orderId",
      nullable = false,
      unique = true,
      foreignKey = @ForeignKey(name = "fk_payment_order"))
  @JsonBackReference("order-payments")
  Order order;
}
