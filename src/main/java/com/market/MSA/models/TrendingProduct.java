package com.market.MSA.models;

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
@Table(name = "trendingproducts")
public class TrendingProduct {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  long trendId;

  double trendScore;
  Date timestamp;

  @ManyToOne
  @JoinColumn(name = "productId", nullable = false)
  Product product;
}
