package com.market.MSA.models.order;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.market.MSA.constants.OrderStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;

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
    name = "cancel_orders",
    indexes = {
      @Index(name = "idx_cancel_order", columnList = "order_id"),
      @Index(name = "idx_cancel_status", columnList = "status"),
      @Index(name = "idx_cancel_date", columnList = "cancelDate")
    })
public class CancelOrder {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long cancelOrderId;

  LocalDateTime cancelDate;

  @Enumerated(EnumType.STRING)
  OrderStatus status;

  String reason;

  @PositiveOrZero
  double refundAmount;

  @ManyToOne
  @JoinColumn(
      name = "order_id",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_cancel_order_order"))
  @JsonBackReference("order-cancel-orders")
  Order order;
}
