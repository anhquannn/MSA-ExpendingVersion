package com.market.MSA.models.product;

import com.market.MSA.models.others.Notification;
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
  Long inventoryId;

  String name;
  String address;
  String contact;
  double totalRevenue;

  @OneToOne
  @JoinColumn(name = "branchId", nullable = false, unique = true)
  Branch branch;

  @OneToMany(mappedBy = "inventory", cascade = CascadeType.ALL, orphanRemoval = true)
  List<InventoryProduct> inventoryProducts;

  @OneToMany(mappedBy = "inventory", cascade = CascadeType.ALL, orphanRemoval = true)
  List<Notification> notifications;

  @OneToMany(mappedBy = "fromInventory", cascade = CascadeType.ALL, orphanRemoval = true)
  List<Transfer> fromTransfers;

  @OneToMany(mappedBy = "toInventory", cascade = CascadeType.ALL, orphanRemoval = true)
  List<Transfer> toTransfers;
}
