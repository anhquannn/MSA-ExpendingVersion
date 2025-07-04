package com.market.MSA.models.product;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.market.MSA.constants.ProductStatus;
import com.market.MSA.models.user.User;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(
    name = "transfer_requests",
    indexes = {
      @Index(name = "idx_transfer_request_from_inventory", columnList = "from_inventory_id"),
      @Index(name = "idx_transfer_request_to_inventory", columnList = "to_inventory_id"),
      @Index(name = "idx_transfer_request_requester", columnList = "requester_id"),
      @Index(name = "idx_transfer_request_approver", columnList = "approver_id")
    })
public class Transfer {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long transferRequestId;

  @ManyToOne
  @JoinColumn(
      name = "from_inventory_id",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_transfer_from_inventory"))
  @JsonBackReference("inventory-from-transfers")
  Inventory fromInventory;

  @ManyToOne
  @JoinColumn(
      name = "to_inventory_id",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_transfer_to_inventory"))
  @JsonBackReference("inventory-to-transfers")
  Inventory toInventory;

  @ManyToOne
  @JoinColumn(
      name = "requester_id",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_transfer_requester"))
  @JsonBackReference("user-requested-transfers")
  User requester;

  @ManyToOne
  @JoinColumn(name = "approver_id", foreignKey = @ForeignKey(name = "fk_transfer_approver"))
  @JsonBackReference("user-approved-transfers")
  User approver;

  @Enumerated(EnumType.STRING)
  ProductStatus status;

  String note;
  LocalDateTime createdAt;
  LocalDateTime updatedAt;

  @OneToMany(mappedBy = "transfer", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonManagedReference("transfer-details")
  List<TransferItem> transferItems = new ArrayList<>();

  @OneToMany(mappedBy = "transfer", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonManagedReference("transfer-inbounds")
  List<InboundTransfer> inboundTransfers = new ArrayList<>();

  @OneToMany(mappedBy = "transfer", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonManagedReference("transfer-details")
  List<OutboundTransfer> outboundTransfers = new ArrayList<>();
}
