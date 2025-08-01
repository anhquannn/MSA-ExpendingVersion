package com.market.MSA.models.order;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.market.MSA.constants.ReturnStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
@Table(name = "returnOrders")
public class ReturnOrder {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long returnOrderId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "order_id",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_return_order_order"))
  @JsonBackReference("order-return-orders")
  Order order;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "user_id",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_return_order_user"))
  @JsonBackReference("user-return-orders")
  com.market.MSA.models.user.User user;

  BigDecimal refundAmount;

  @Enumerated(EnumType.STRING)
  ReturnStatus status;

  String shippingCode;
  String shippingStatus;

  String reason;

  LocalDateTime createdAt;
  LocalDateTime updatedAt;

  @OneToMany(mappedBy = "returnOrder", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonManagedReference("return-order-items")
  @Builder.Default
  List<ReturnOrderItem> returnOrderItems = new ArrayList<>();
}
