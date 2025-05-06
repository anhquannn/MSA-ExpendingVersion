package com.market.MSA.models.user;

import com.market.MSA.models.order.Cart;
import com.market.MSA.models.order.Order;
import com.market.MSA.models.others.Notification;
import com.market.MSA.models.others.Payment;
import com.market.MSA.models.product.Branch;
import com.market.MSA.models.product.Feedback;
import com.market.MSA.models.product.Transfer;
import com.market.MSA.validators.PhoneNumberConstraint;
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
@Table(
    name = "users",
    indexes = {
      @Index(name = "idx_user_email", columnList = "email", unique = true),
      @Index(name = "idx_user_phone", columnList = "phone_number")
    })
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long userId;

  String fullName;

  @Column(
      name = "email",
      unique = true,
      columnDefinition = "varchar(255) collate utf8mb4_unicode_ci")
  String email;

  @PhoneNumberConstraint String phoneNumber;

  String birthday;

  String password;
  String address;
  String deviceId;
  String image;
  String googleId;

  @ManyToMany Set<Role> roles;

  @ManyToMany List<Branch> branches;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  List<Feedback> feedbacks;

  @OneToMany(mappedBy = "requester", cascade = CascadeType.ALL, orphanRemoval = true)
  List<Transfer> fromTransfers;

  @OneToMany(mappedBy = "approver", cascade = CascadeType.ALL, orphanRemoval = true)
  List<Transfer> toTransfers;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  List<Cart> carts;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  List<Payment> payments;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  List<Order> orders;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  List<Notification> notifications;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  List<UserBehavior> userBehaviors;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  List<RewardPoint> rewardPoints;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  List<RewardPointTransaction> rewardPointTransactions;
}
