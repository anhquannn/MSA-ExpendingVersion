package com.market.MSA.models.order;

import com.market.MSA.constants.Condition;
import com.market.MSA.constants.OrderStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "return_orders")
public class ReturnOrders {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long returnOrdersId;

  LocalDate returnOrderDate;
  int returnOrderQuantity;
  Condition returnOrderCondition;

  @Positive double returnAmount;

  OrderStatus returnOrderStatus;
  String reason;
}
