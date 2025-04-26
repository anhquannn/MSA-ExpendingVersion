package com.market.MSA.models.product;

import com.market.MSA.models.user.User;
import jakarta.persistence.*;
import java.util.Date;
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
  @JoinColumn(name = "fromInventoryId", nullable = false)
  Inventory fromInventory;

  @ManyToOne
  @JoinColumn(name = "toInventoryId", nullable = false)
  Inventory toInventory;

  @ManyToOne
  @JoinColumn(name = "requesterId", nullable = false)
  User requester;

  @ManyToOne
  @JoinColumn(name = "approverId")
  User approver;

  String status;
  String note;
  Date createdAt;
  Date updatedAt;

  @OneToMany(mappedBy = "transfer", cascade = CascadeType.ALL, orphanRemoval = true)
  List<TransferItem> transferItems;
}
