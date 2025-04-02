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
@Table(name = "inventoryProduct")
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
