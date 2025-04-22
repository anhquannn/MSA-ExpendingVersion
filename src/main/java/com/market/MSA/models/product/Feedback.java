package com.market.MSA.models.product;

import com.market.MSA.models.user.User;
import com.market.MSA.validators.RatingConstraint;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.Date;
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

  String comment;

  @RatingConstraint int rating;

  Date createAt;

  @ManyToOne
  @JoinColumn(name = "userId", nullable = false)
  User user;

  @ManyToOne
  @JoinColumn(name = "productId", nullable = false)
  Product product;
}
