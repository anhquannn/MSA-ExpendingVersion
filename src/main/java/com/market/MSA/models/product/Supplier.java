package com.market.MSA.models.product;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
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
@Table(name = "suppliers")
public class Supplier {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long supplierId;

  String name;
  String address;
  String contact;
  String image;

  @OneToMany(mappedBy = "supplier", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonManagedReference("supplier-products")
  List<Product> products = new ArrayList<>();
}
