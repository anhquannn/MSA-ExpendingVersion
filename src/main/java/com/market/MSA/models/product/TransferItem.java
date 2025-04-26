package com.market.MSA.models.product;

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
  @JoinColumn(name = "transferRequestId", nullable = false)
  Transfer transfer;

  @ManyToOne
  @JoinColumn(name = "productId", nullable = false)
  Product product;

  int quantityRequested;
  int quantityTransferred;
}
