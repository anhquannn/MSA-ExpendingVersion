package com.market.MSA.models.product;

import com.market.MSA.validators.StockNumberConstraint;
import jakarta.persistence.*;
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

  String stockLevel;

  @StockNumberConstraint int stockNumber;

  @ManyToOne
  @JoinColumn(name = "inventoryId", nullable = false)
  Inventory inventory;

  @ManyToOne
  @JoinColumn(name = "productId", nullable = false)
  Product product;
}
