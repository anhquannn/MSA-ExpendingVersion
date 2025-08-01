package com.market.MSA.models.order;

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
@Table(name = "returnItemImages")
public class ReturnItemImage {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long imageId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "item_id", nullable = false, foreignKey = @ForeignKey(name = "fk_rimg_item"))
  @JsonBackReference("return-item-images")
  ReturnOrderItem returnOrderItem;

  @Column(nullable = false)
  String imageUrl;

  @Builder.Default LocalDateTime createdAt = LocalDateTime.now();
}
