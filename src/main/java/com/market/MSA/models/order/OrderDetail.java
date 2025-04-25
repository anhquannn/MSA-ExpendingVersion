package com.market.MSA.models.order;

import com.market.MSA.models.product.Product;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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

  int quantity;
  double unitPrice;
  double totalPrice;
  String image;

  @ManyToOne
  @JoinColumn(name = "orderId", nullable = false)
  Order order;

  @ManyToOne
  @JoinColumn(name = "productId", nullable = false)
  Product product;
}
