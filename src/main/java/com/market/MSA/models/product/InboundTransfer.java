package com.market.MSA.models.product;

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
@Table(name = "inbound_transfer")
public class InboundTransfer {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long inboundTransferId;

  String status;
  LocalDateTime inboundTransferDate;

  @ManyToOne
  @JoinColumn(name = "transferRequestId", nullable = false)
  @JsonBackReference("transfer-inbounds")
  Transfer transfer;

  @ManyToOne
  @JoinColumn(name = "inventoryId", nullable = false)
  @JsonBackReference("inventory-inbounds")
  Inventory Inventory;

  @ManyToOne
  @JoinColumn(name = "userId", nullable = false)
  @JsonBackReference("user-requested-inbounds")
  User user;
}
