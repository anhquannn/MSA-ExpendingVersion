package com.market.MSA.models.order;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.market.MSA.models.product.Product;
import jakarta.persistence.*;
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
  @JoinColumn(
      name = "product_id",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_cart_item_product"))
  @JsonBackReference("product-cart-items")
  Product product;

  @ManyToOne
  @JoinColumn(
      name = "cart_id",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_cart_item_cart"))
  @JsonBackReference("cart-items")
  Cart cart;
}
