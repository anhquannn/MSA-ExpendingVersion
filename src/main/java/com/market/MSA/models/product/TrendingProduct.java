package com.market.MSA.models.product;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.time.LocalDateTime;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "trendingProducts")
public class TrendingProduct {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long trendId;

  @Positive
  double trendScore;
  LocalDateTime timestamp;

  @ManyToOne
  @JoinColumn(
      name = "product_id",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_trending_product"))
  @JsonBackReference("product-trending")
  Product product;
}
