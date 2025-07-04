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
@Table(name = "checked_histories")
public class CheckedHistory {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long checkedHistoryId;

  LocalDateTime checkedDate;
  String note;

  @ManyToOne
  @JoinColumn(
      name = "user_id",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_checked_history_user"))
  @JsonBackReference("user-checked-histories")
  User user;

  @ManyToOne
  @JoinColumn(
      name = "inventory_id",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_checked_history_inventory"))
  @JsonBackReference("inventory-checked-histories")
  Inventory inventory;
}
