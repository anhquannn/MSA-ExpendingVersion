package com.market.MSA.models.others;

import com.market.MSA.models.order.Order;
import com.market.MSA.models.user.User;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
  String status;
  double grandTotal;
  String transactionId;

  String bankCode;
  String bankTranNo;
  String responseCode;
  Date updateDate;

  @ManyToOne
  @JoinColumn(name = "userId", nullable = false)
  User user;

  @ManyToOne
  @JoinColumn(name = "orderId", nullable = false)
  Order order;
}
