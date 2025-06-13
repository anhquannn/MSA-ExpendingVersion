package com.market.MSA.models.order;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.market.MSA.models.user.User;
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
@Table(name = "promo_code_usages")
public class PromoCodeUsage {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long promoCodeUsageId;

  LocalDateTime usedAt;

  @ManyToOne
  @JoinColumn(name = "promoCodeId", nullable = false)
  @JsonBackReference("promocode-usages")
  PromoCode promoCode;

  @ManyToOne
  @JoinColumn(name = "orderId", nullable = false)
  @JsonBackReference("order-promo-usages")
  Order order;

  @ManyToOne
  @JoinColumn(name = "userId", nullable = false)
  @JsonBackReference("user-promo-usages")
  User user;
}
