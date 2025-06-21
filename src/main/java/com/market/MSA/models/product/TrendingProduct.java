package com.market.MSA.models.product;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
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

  double trendScore;
  LocalDateTime timestamp;

  @ManyToOne
  @JoinColumn(name = "productId", nullable = false)
  @JsonBackReference("product-trending")
  Product product;
}
