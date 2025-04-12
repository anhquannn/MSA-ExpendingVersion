package com.market.MSA.models;

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
      @Index(name = "idx_invproduct_stock", columnList = "stockLevel"),
      @Index(name = "idx_invproduct_number", columnList = "stockNumber")
    })
public class InventoryProduct {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  long inventoryProductId;

  int stockNumber;
  String stockLevel;

  @ManyToOne
  @JoinColumn(name = "inventoryId", nullable = false)
  Inventory inventory;

  @ManyToOne
  @JoinColumn(name = "productId", nullable = false)
  Product product;
}
