package com.market.MSA.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
@Table(name = "feedbacks")
public class Feedback {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  long feedbackId;

  int rating;
  String comments;

  @ManyToOne
  @JoinColumn(name = "userId", nullable = false)
  User user;

  @ManyToOne
  @JoinColumn(name = "productId", nullable = false)
  Product product;
}
