package com.market.MSA.models.product;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
@Table(name = "product_combinations")
public class ProductCombination {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long combinationId;

  @ManyToOne
  @JoinColumn(name = "productId1")
  @JsonBackReference("comb-product1")
  Product product1;

  @ManyToOne
  @JoinColumn(name = "productId2")
  @JsonBackReference("comb-product2")
  Product product2;
}
