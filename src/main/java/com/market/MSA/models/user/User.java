package com.market.MSA.models.user;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.market.MSA.models.order.Cart;
import com.market.MSA.models.order.Order;
import com.market.MSA.models.order.PromoCodeUsage;
import com.market.MSA.models.others.Notification;
import com.market.MSA.models.others.Payment;
import com.market.MSA.models.product.*;
import com.market.MSA.validators.PhoneNumberConstraint;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
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

  LocalDateTime birthday;

  String password;
  String deviceId;

  @Column(columnDefinition = "TEXT")
  String image;

  String googleId;

  @ManyToMany
  @JoinTable(
      name = "user_roles",
      joinColumns = @JoinColumn(name = "user_id"),
      inverseJoinColumns = @JoinColumn(name = "role_id"))
  @JsonManagedReference("user-roles")
  Set<Role> roles = new HashSet<>();

  @ManyToMany
  @JoinTable(
      name = "user_branches",
      joinColumns = @JoinColumn(name = "user_id"),
      inverseJoinColumns = @JoinColumn(name = "branch_id"))
  @JsonManagedReference("user-branches")
  List<Branch> branches = new ArrayList<>();

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonManagedReference("user-feedbacks")
  List<Feedback> feedbacks = new ArrayList<>();

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonManagedReference("user-checked-histories")
  List<CheckedHistory> checkedHistories = new ArrayList<>();

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonManagedReference("user-requested-inbounds")
  List<InboundTransfer> inboundTransfers = new ArrayList<>();

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonManagedReference("user-approved-outbounds")
  List<OutboundTransfer> outTransfers = new ArrayList<>();

  @OneToMany(mappedBy = "requester", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonManagedReference("user-requested-transfers")
  List<Transfer> fromTransfers = new ArrayList<>();

  @OneToMany(mappedBy = "approver", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonManagedReference("user-approved-transfers")
  List<Transfer> toTransfers = new ArrayList<>();

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonManagedReference("user-carts")
  List<Cart> carts = new ArrayList<>();

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonManagedReference("user-payments")
  List<Payment> payments = new ArrayList<>();

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonManagedReference("user-orders")
  List<Order> orders = new ArrayList<>();

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonManagedReference("user-notifications")
  List<Notification> notifications = new ArrayList<>();

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonManagedReference("user-behaviors")
  List<UserBehavior> userBehaviors = new ArrayList<>();

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonManagedReference("user-reward-points")
  List<RewardPoint> rewardPoints = new ArrayList<>();

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonManagedReference("user-reward-transactions")
  List<RewardPointTransaction> rewardPointTransactions = new ArrayList<>();

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonManagedReference("user-addresses")
  List<UserAddress> userAddresses = new ArrayList<>();

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonManagedReference("user-promo-usages")
  List<PromoCodeUsage> promoCodeUsages = new ArrayList<>();

  @OneToMany(mappedBy = "surveyor", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonManagedReference("icr-surveyor")
  List<InventoryCheckRequest> inventoryCheckRequestS = new ArrayList<>();

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonManagedReference("icr-user")
  List<InventoryCheckRequest> inventoryCheckRequests = new ArrayList<>();
}
