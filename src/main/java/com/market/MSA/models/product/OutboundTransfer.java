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
@Table(name = "outbound_transfer")
public class OutboundTransfer {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long outboundTransferId;

  @Enumerated(EnumType.STRING)
  ProductStatus status;

  LocalDateTime outboundTransferDate;

  @OneToOne
  @JoinColumn(
      name = "transfer_request_id",
      nullable = false,
      unique = true,
      foreignKey = @ForeignKey(name = "fk_outbound_transfer_request"))
  @JsonBackReference("transfer-outbound")
  Transfer transfer;

  @ManyToOne
  @JoinColumn(
      name = "inventory_id",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_outbound_transfer_inventory"))
  @JsonBackReference("inventory-outbounds")
  Inventory inventory;

  @ManyToOne
  @JoinColumn(
      name = "user_id",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_outbound_transfer_user"))
  @JsonBackReference("user-approved-outbounds")
  User user;
}
