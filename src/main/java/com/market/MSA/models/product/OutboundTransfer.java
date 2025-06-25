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
@Table(name = "outbound_transfer")
public class OutboundTransfer {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long outboundTransferId;

  String status;
  LocalDateTime outboundTransferDate;

  @ManyToOne
  @JoinColumn(name = "transferRequestId", nullable = false)
  @JsonBackReference("transfer-outbounds")
  Transfer transfer;

  @ManyToOne
  @JoinColumn(name = "inventoryId", nullable = false)
  @JsonBackReference("inventory-outbounds")
  Inventory Inventory;

  @ManyToOne
  @JoinColumn(name = "userId", nullable = false)
  @JsonBackReference("user-approved-outbounds")
  User user;
}
