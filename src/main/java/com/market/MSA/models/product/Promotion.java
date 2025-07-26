package com.market.MSA.models.product;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.market.MSA.constants.PromocodeStatus;
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
@Table(
    name = "promotions",
    uniqueConstraints =
        @UniqueConstraint(
            name = "uk_promo_main_free",
            columnNames = {"productMain_id", "productFree_id"}))
public class Promotion {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long promotionId;

  @Column(nullable = false)
  LocalDateTime startDate;

  @Column(nullable = false)
  LocalDateTime endDate;

  @Builder.Default
  @Column(nullable = false)
  Integer discountPercentage = 100;

  PromocodeStatus status;

  @ManyToOne
  @JoinColumn(name = "productMain_id", foreignKey = @ForeignKey(name = "fk_promo-productMain"))
  @JsonBackReference("promo-productMain")
  Product productMain;

  @ManyToOne
  @JoinColumn(name = "productFree_id", foreignKey = @ForeignKey(name = "fk_promo-productFree"))
  @JsonBackReference("promo-productFree")
  Product productFree;
}
