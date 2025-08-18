package com.market.MSA.models.order;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import java.util.ArrayList;
import java.util.List;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Fetch;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "returnOrderItems")
public class ReturnOrderItem {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long itemId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "return_order_id",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_roi_return"))
  @JsonBackReference("return-order-items")
  ReturnOrder returnOrder;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "order_detail_id",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_roi_detail"))
  @JsonBackReference("order-detail-return-items")
  OrderDetail orderDetail;

  @Positive(message = "Số lượng phải > 0")
  int quantity;

  String conditionNote;
  String reason;

  @OneToMany(
      mappedBy = "returnOrderItem",
      cascade = CascadeType.ALL,
      orphanRemoval = true,
      fetch = FetchType.LAZY)
  @Fetch(org.hibernate.annotations.FetchMode.SUBSELECT)
  @JsonManagedReference("return-item-images")
  @Builder.Default
  List<ReturnItemImage> images = new ArrayList<>();
}
