package com.market.MSA.models.product;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.market.MSA.models.order.OrderDetail;
import com.market.MSA.models.user.User;
import com.market.MSA.validators.RatingConstraint;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(
    name = "feedbacks",
    indexes = {
      @Index(name = "idx_feedback_user", columnList = "user_id"),
      @Index(name = "idx_feedback_product", columnList = "product_id"),
      @Index(name = "idx_feedback_rating", columnList = "rating")
    })
public class Feedback {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long feedbackId;

  String comments;

  @RatingConstraint int rating;

  LocalDateTime createdAt;

  @ManyToOne
  @JoinColumn(
      name = "user_id",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_feedback_user"))
  @JsonBackReference("user-feedbacks")
  User user;

  @ManyToOne
  @JoinColumn(
      name = "product_id",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_feedback_product"))
  @JsonBackReference("product-feedbacks")
  Product product;

  @OneToOne
  @JoinColumn(
      name = "order_detail_id",
      unique = true,
      foreignKey = @ForeignKey(name = "fk_feedback_order_detail"))
  @JsonBackReference("orderDetail-feedback")
  OrderDetail orderDetail;
}
