package com.market.MSA.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "payments")
public class Payment {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  long paymentId;

  String paymentMethod;
  Date paymentDate;
  String status;
  double grandTotal;

  @ManyToOne
  @JoinColumn(name = "userId", nullable = false)
  User user;

  @ManyToOne
  @JoinColumn(name = "orderId", nullable = false)
  Order order;
}
