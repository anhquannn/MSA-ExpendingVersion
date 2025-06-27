package com.market.MSA.models.product;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.market.MSA.models.others.Notification;
import jakarta.persistence.*;
import java.util.ArrayList;
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
  @JsonBackReference("branch-inventory")
  Branch branch;

  @OneToMany(mappedBy = "inventory", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonManagedReference("inventory-products")
  List<InventoryProduct> inventoryProducts = new ArrayList<>();

  @OneToMany(mappedBy = "inventory", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonManagedReference("inventory-notifications")
  List<Notification> notifications = new ArrayList<>();

  @OneToMany(mappedBy = "fromInventory", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonManagedReference("inventory-from-transfers")
  List<Transfer> fromTransfers = new ArrayList<>();

  @OneToMany(mappedBy = "toInventory", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonManagedReference("inventory-to-transfers")
  List<Transfer> toTransfers = new ArrayList<>();

  @OneToMany(mappedBy = "inventory", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonManagedReference("inventory-inbounds")
  List<InboundTransfer> inboundTransfers = new ArrayList<>();

  @OneToMany(mappedBy = "inventory", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonManagedReference("inventory-outbounds")
  List<OutboundTransfer> outboundTransfers = new ArrayList<>();

  @OneToMany(mappedBy = "inventory", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonManagedReference("inventory-checked-histories")
  List<CheckedHistory> checkedHistories = new ArrayList<>();

  @OneToMany(mappedBy = "inventory", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonManagedReference("icr-inventory")
  List<InventoryCheckRequest> inventoryCheckRequests = new ArrayList<>();
}
