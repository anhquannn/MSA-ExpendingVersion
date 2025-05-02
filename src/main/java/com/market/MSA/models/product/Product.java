package com.market.MSA.models.product;

import com.market.MSA.models.order.CartItem;
import com.market.MSA.models.order.OrderDetail;
import com.market.MSA.models.others.Notification;
import com.market.MSA.models.user.UserBehavior;
import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import java.util.Date;
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
@Table(
    name = "products",
    indexes = {
      @Index(name = "idx_product_name", columnList = "name"),
      @Index(name = "idx_product_manufacturer", columnList = "manufacture_id"),
      @Index(name = "idx_product_category", columnList = "category_id")
    })
public class Product {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long productId;

  String name;
  String images;

  @Positive double price;

  @Positive double currentPrice;

  String unit;
  String color;
  String specification;
  String description;
  Date expiry;
  Date createAt;

  @Positive double totalRevenue;

  @ManyToOne
  @JoinColumn(name = "manufactureId", nullable = false)
  Manufacturer manufacturer;

  @ManyToOne
  @JoinColumn(name = "categoryId", nullable = false)
  Category category;

  @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
  List<CartItem> cartItems;

  @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
  List<Feedback> feedbacks;

  @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
  List<TransferItem> transferItems;

  @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
  List<OrderDetail> orderDetails;

  @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
  List<InventoryProduct> inventoryProducts;

  @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
  List<UserBehavior> userBehaviors;

  @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
  List<Notification> notifications;

  @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
  List<TrendingProduct> trendingProducts;
}
