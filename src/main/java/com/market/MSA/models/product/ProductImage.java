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
@Table(
    name = "product_images",
    indexes = {@Index(name = "idx_product_image", columnList = "product_id")})
public class ProductImage {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long productImageId;

  String imageUrl;
  boolean isPrimary;
  int sortOrder;

  @ManyToOne
  @JoinColumn(name = "productId", nullable = false)
  @JsonBackReference("product-images")
  Product product;
}
