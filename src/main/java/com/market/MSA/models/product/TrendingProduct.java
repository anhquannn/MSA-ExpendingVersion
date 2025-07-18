package com.market.MSA.models.product;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
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

  @PositiveOrZero(message = "Điểm xu hướng phải >= 0")
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
