package com.market.MSA.models;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.Date;
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
@Table(name = "products")
public class Product {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  long productId;

  String name;
  String image;
  double price;
  int size;
  String color;
  String specification;
  String description;
  Date expiry;
  int stockNumber;
  String stockLevel;
  int sales;

  @ManyToOne
  @JoinColumn(name = "manufactureId", nullable = false)
  Manufacturer manufacturer;

  @ManyToOne
  @JoinColumn(name = "branchId", nullable = false)
  Branch branch;

  @ManyToOne
  @JoinColumn(name = "categoryId", nullable = false)
  Category category;

  @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
  List<CartItem> cartitems;

  @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
  List<Feedback> feedbacks;

  @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
  List<OrderDetail> orderDetails;
}
