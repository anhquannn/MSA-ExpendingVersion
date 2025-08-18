package com.market.MSA.models.product;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.market.MSA.constants.ABCClassification;
import com.market.MSA.models.order.CartItem;
import com.market.MSA.models.order.OrderDetail;
import com.market.MSA.models.others.Notification;
import com.market.MSA.models.user.UserBehavior;
import jakarta.persistence.*;
import jakarta.validation.constraints.PositiveOrZero;
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
@Table(
    name = "products",
    indexes = {
      @Index(name = "idx_product_name", columnList = "name"),
      @Index(name = "idx_product_supplier", columnList = "supplier_id"),
      @Index(name = "idx_product_category", columnList = "category_id")
    })
public class Product {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long productId;

  @Column(nullable = false, unique = true)
  String name;

  @PositiveOrZero(message = "Giá sản phẩm phải >= 0")
  double price;

  double discountPercentage;
  int discountTriggerDays;
  String unit;
  String netWeight;
  String specification;
  String description;
  LocalDateTime createdAt;
  LocalDateTime lastClassificationDate;

  boolean isPromotional;
  boolean isExemptFromPromotion;
  ABCClassification abcClassification;

  @PositiveOrZero double totalRevenue;

  @ManyToOne
  @JoinColumn(
      name = "supplier_id",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_product_supplier"))
  @JsonBackReference("supplier-products")
  Supplier supplier;

  @ManyToOne
  @JoinColumn(
      name = "category_id",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_product_category"))
  @JsonBackReference("product-categories")
  Category category;

  @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonManagedReference("product-cart-items")
  List<CartItem> cartItems = new ArrayList<>();

  @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonManagedReference("product-feedbacks")
  List<Feedback> feedbacks = new ArrayList<>();

  @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonManagedReference("product-images")
  List<ProductImage> images = new ArrayList<>();

  @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonManagedReference("product-transfer-items")
  List<TransferItem> transferItems = new ArrayList<>();

  @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonManagedReference("product-order-details")
  List<OrderDetail> orderDetails = new ArrayList<>();

  @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonManagedReference("product-inventories")
  List<InventoryProduct> inventoryProducts = new ArrayList<>();

  @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonManagedReference("product-behaviors")
  List<UserBehavior> userBehaviors = new ArrayList<>();

  @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonManagedReference("product-notifications")
  List<Notification> notifications = new ArrayList<>();

  @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonManagedReference("product-trending")
  List<TrendingProduct> trendingProducts = new ArrayList<>();

  @OneToMany(mappedBy = "product1", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonManagedReference("comb-product1")
  List<ProductCombination> product1s = new ArrayList<>();

  @OneToMany(mappedBy = "product2", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonManagedReference("comb-product2")
  List<ProductCombination> product2s = new ArrayList<>();

  @OneToMany(mappedBy = "productMain", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonManagedReference("promo-productMain")
  List<Promotion> productMains = new ArrayList<>();

  @OneToMany(mappedBy = "productFree", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonManagedReference("promo-productFree")
  List<Promotion> productFrees = new ArrayList<>();
}
