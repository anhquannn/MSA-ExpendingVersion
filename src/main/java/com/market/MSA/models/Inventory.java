package com.market.MSA.models;

import jakarta.persistence.*;
import java.util.List;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "inventories")
public class Inventory {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  long inventoryId;

  String name;
  String address;
  String contact;
  long totalRevenue;

  @ManyToOne
  @JoinColumn(name = "branchId", nullable = false)
  Branch branch;

  @OneToMany(mappedBy = "inventory", cascade = CascadeType.ALL, orphanRemoval = true)
  List<InventoryProduct> inventoryProducts;
}
