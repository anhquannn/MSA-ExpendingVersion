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

@Entity
@Table(name = "cartItems")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartItem {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long cartItemId;

  boolean isSelected;
  double price;
  int quantity;

  @ManyToOne
  @JoinColumn(name = "productId", nullable = false)
  Product product;

  @ManyToOne
  @JoinColumn(name = "cartId", nullable = false)
  Cart cart;
}
