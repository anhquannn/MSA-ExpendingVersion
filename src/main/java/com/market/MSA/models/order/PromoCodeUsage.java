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
  @JoinColumn(
      name = "promo_code_id",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_promo_code_usage_promocode"))
  @JsonBackReference("promoCode-usages")
  PromoCode promoCode;

  @ManyToOne
  @JoinColumn(
      name = "order_id",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_promo_code_usage_order"))
  @JsonBackReference("order-promo-usages")
  Order order;

  @ManyToOne
  @JoinColumn(
      name = "user_id",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_promo_code_usage_user"))
  @JsonBackReference("user-promo-usages")
  User user;
}
