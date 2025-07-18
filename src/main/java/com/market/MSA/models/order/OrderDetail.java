package com.market.MSA.models.order;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.market.MSA.constants.OrderStatus;
import com.market.MSA.models.product.Product;
import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
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
@Table(name = "orderDetails")
public class OrderDetail {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long orderDetailId;

  @Positive(message = "Số lượng phải > 0")
  int quantity;

  String name;

  @Enumerated(EnumType.STRING)
  OrderStatus status;

  @PositiveOrZero(message = "Đơn giá phải >= 0")
  double unitPrice;

  @PositiveOrZero(message = "Tổng giá phải >= 0")
  double totalPrice;

  String image;
  boolean rated;

  @ManyToOne
  @JoinColumn(
      name = "order_id",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_order_detail_order"))
  @JsonBackReference("order-details")
  Order order;

  @ManyToOne
  @JoinColumn(
      name = "product_id",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_order_detail_product"))
  @JsonBackReference("product-order-details")
  Product product;
}
