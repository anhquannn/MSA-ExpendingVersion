package com.market.MSA.models.product;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.market.MSA.constants.ProductStatus;
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
@Table(name = "inventory_check_requests")
public class InventoryCheckRequest {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long icrId;

  LocalDateTime requestedDate;

  @Enumerated(EnumType.STRING)
  ProductStatus status;

  String note;

  // relationships
  @ManyToOne
  @JoinColumn(name = "inventoryId", nullable = false)
  @JsonBackReference("icr-inventory")
  Inventory inventory;

  @ManyToOne
  @JoinColumn(name = "surveyorId", nullable = false)
  @JsonBackReference("icr-surveyor")
  User surveyor;

  @ManyToOne
  @JoinColumn(name = "userId", nullable = false)
  @JsonBackReference("icr-user")
  User user;
}
