package com.market.MSA.models.product;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.market.MSA.validators.StockNumberConstraint;
import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(
    name = "inventory_products",
    indexes = {
      @Index(name = "idx_invproduct_inventory", columnList = "inventory_id"),
      @Index(name = "idx_invproduct_product", columnList = "product_id"),
      @Index(name = "idx_invproduct_stock", columnList = "stock_level"),
      @Index(name = "idx_invproduct_number", columnList = "stock_number")
    })
public class InventoryProduct {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long inventoryProductId;

  @PositiveOrZero(message = "Current price must be positive or zero")
  double currentPrice;

  @Future(message = "Expiration date must be in the future")
  LocalDateTime expDate;

  boolean isActive;
  boolean isDiscounted;

  @Size(max = 50, message = "Batch number must be less than 50 characters")
  String batchNumber;

  String stockLevel;

  @StockNumberConstraint
  @PositiveOrZero(message = "Stock number must be positive or zero")
  int stockNumber;

  @StockNumberConstraint
  @PositiveOrZero(message = "Stock number must be positive or zero")
  Integer stockNumberChecked;

  @StockNumberConstraint
  @PositiveOrZero(message = "Stock number must be positive or zero")
  int stockNumberDifferent;

  @ManyToOne
  @JoinColumn(name = "inventoryId", nullable = false)
  @JsonBackReference("inventory-products")
  @NotNull(message = "Inventory is required")
  Inventory inventory;

  @ManyToOne
  @JoinColumn(
      name = "productId",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_invproduct_product"))
  @JsonBackReference("product-inventories")
  @NotNull(message = "Product is required")
  Product product;
}
