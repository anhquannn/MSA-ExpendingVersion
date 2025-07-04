package com.market.MSA.models.product;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
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
    name = "transfer_request_items",
    indexes = {
      @Index(
          name = "idx_transfer_request_item_transfer_request",
          columnList = "transfer_request_id"),
      @Index(name = "idx_transfer_request_item_product", columnList = "product_id")
    })
public class TransferItem {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long transferRequestItemId;

  @ManyToOne
  @JoinColumn(
      name = "transfer_request_id",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_transfer_item_transfer"))
  @JsonBackReference("transfer-details")
  Transfer transfer;

  @ManyToOne
  @JoinColumn(
      name = "product_id",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_transfer_item_product"))
  @JsonBackReference("product-transfer-items")
  Product product;

  int quantityRequested;
  int quantityTransferred;
}
