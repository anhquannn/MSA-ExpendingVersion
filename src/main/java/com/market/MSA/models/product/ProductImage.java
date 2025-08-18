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

  @Column(columnDefinition = "TEXT")
  String imageUrl;

  boolean isPrimary;
  int sortOrder;

  @ManyToOne
  @JoinColumn(
      name = "product_id",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_product_image_product"))
  @JsonBackReference("product-images")
  Product product;
}
