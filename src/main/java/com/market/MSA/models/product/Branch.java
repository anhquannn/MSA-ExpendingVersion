package com.market.MSA.models.product;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.market.MSA.models.order.Order;
import com.market.MSA.models.user.User;
import jakarta.persistence.*;
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

  @Column(nullable = false)
  String cityCode;

  @Column(nullable = false)
  String districtCode;

  @Column(nullable = false)
  String wardCode;

  @OneToMany(mappedBy = "branch", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonManagedReference("order-branches")
  List<Order> orders = new ArrayList<>();

  @OneToOne(mappedBy = "branch", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonManagedReference("branch-inventory")
  Inventory inventory;

  @ManyToMany(mappedBy = "branches")
  @JsonBackReference("user-branches")
  List<User> users = new ArrayList<>();
}
