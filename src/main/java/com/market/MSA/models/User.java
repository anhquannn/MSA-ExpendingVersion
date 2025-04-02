package com.market.MSA.models;

import jakarta.persistence.*;
import java.util.List;
import java.util.Set;
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
@Table(name = "users")
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  long userId;

  String fullName;

  @Column(
      name = "email",
      unique = true,
      columnDefinition = "varchar(255) collate utf8mb4_unicode_ci")
  String email;

  String phoneNumber;
  String birthday;
  String password;
  String address;
  String googleId;

  @ManyToMany Set<Role> roles;

  @ManyToOne
  @JoinColumn(name = "branchId", nullable = false)
  Branch branch;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  List<Feedback> feedbacks;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  List<Cart> carts;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  List<Payment> payments;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  List<Order> orders;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  List<UserBehavior> userBehaviors;

  // Danh sách yêu cầu điều hàng mà user này tạo
  @OneToMany(mappedBy = "userRequest", cascade = CascadeType.ALL, orphanRemoval = true)
  List<StockTransfer> stockTransferRequests;

  // Danh sách yêu cầu điều hàng mà user này xác nhận
  @OneToMany(mappedBy = "userResponse", cascade = CascadeType.ALL, orphanRemoval = true)
  List<StockTransfer> stockTransferResponses;
}
