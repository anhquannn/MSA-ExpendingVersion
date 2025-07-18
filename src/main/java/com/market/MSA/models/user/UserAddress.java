package com.market.MSA.models.user;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
@Table(name = "user_addresses")
public class UserAddress {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long userAddressId;

  String city;
  String district;
  String ward;

  @Column(nullable = false)
  String cityCode;

  @Column(nullable = false)
  String districtCode;

  @Column(nullable = false)
  String wardCode;

  String street;
  boolean isPrimary;
  LocalDateTime createdAt;

  @ManyToOne
  @JoinColumn(
      name = "user_id",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_user_address_user"))
  @JsonBackReference("user-addresses")
  User user;
}
