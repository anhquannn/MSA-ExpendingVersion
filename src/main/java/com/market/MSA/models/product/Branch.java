package com.market.MSA.models.product;

import com.market.MSA.models.order.Order;
import jakarta.persistence.*;
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
@Table(name = "branches")
public class Branch {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long branchId;

  String name;
  String phone;
  String street;
  String ward;
  String district;
  String city;

  @OneToMany(mappedBy = "branch", cascade = CascadeType.ALL, orphanRemoval = true)
  List<Order> orders;

  @OneToOne(mappedBy = "branch", cascade = CascadeType.ALL, orphanRemoval = true)
  Inventory inventory;
}
