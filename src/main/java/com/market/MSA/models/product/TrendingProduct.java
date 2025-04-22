package com.market.MSA.models.product;

import jakarta.persistence.*;
import java.util.Date;
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
  Date timestamp;

  @ManyToOne
  @JoinColumn(name = "productId", nullable = false)
  Product product;
}
