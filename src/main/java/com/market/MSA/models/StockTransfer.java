package com.market.MSA.models;

import jakarta.persistence.*;
import java.util.Date;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "stocktransfers")
public class StockTransfer {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  long stocktransferId;

  int quantity;
  String status;
  Date requestDate;
  Date confirmationDate;

  // Liên kết với chi nhánh gửi hàng (From_Branch)
  @ManyToOne
  @JoinColumn(name = "fromBranchId", nullable = false)
  Branch fromBranch;

  // Liên kết với chi nhánh nhận hàng (To_Branch)
  @ManyToOne
  @JoinColumn(name = "toBranchId", nullable = false)
  Branch toBranch;

  // Liên kết với sản phẩm
  @ManyToOne
  @JoinColumn(name = "productId", nullable = false)
  Product product;

  // Liên kết với người dùng tạo yêu cầu
  @ManyToOne
  @JoinColumn(name = "userRequestId", nullable = false)
  User userRequest;

  // Liên kết với người dùng xác nhận yêu cầu
  @ManyToOne
  @JoinColumn(name = "userResponseId")
  User userResponse;
}
