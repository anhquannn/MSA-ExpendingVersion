package com.market.MSA.models.order;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.market.MSA.constants.CartStatus;
import com.market.MSA.models.user.User;
import jakarta.persistence.*;
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
@Table(name = "carts")
public class Cart {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long cartId;

  @Enumerated(EnumType.STRING)
  CartStatus status;

  @OneToOne
  @JoinColumn(
      name = "user_id",
      nullable = false,
      unique = true,
      foreignKey = @ForeignKey(name = "fk_cart_user"))
  @JsonBackReference("user-cart")
  User user;

  @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonManagedReference("cart-items")
  List<CartItem> cartItems = new ArrayList<>();

  @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonManagedReference("order-carts")
  List<Order> orders = new ArrayList<>();
}
