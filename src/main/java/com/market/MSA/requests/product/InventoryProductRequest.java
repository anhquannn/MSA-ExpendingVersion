package com.market.MSA.requests.product;

import com.market.MSA.validators.StockNumberConstraint;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InventoryProductRequest {
  @PositiveOrZero(message = "Current price must be positive or zero")
  double currentPrice;

  @Future(message = "Expiration date must be in the future")
  LocalDateTime expDate;

  boolean isActive;
  boolean isDiscounted;

  @Size(max = 50, message = "Batch number must be less than 50 characters")
  String batchNumber;

  String stockLevel;

  @StockNumberConstraint
  @PositiveOrZero(message = "Stock number must be positive or zero")
  int stockNumber;

  @PositiveOrZero(message = "Stock number checked must be positive or zero")
  Integer stockNumberChecked;

  @StockNumberConstraint
  @PositiveOrZero(message = "Stock number must be positive or zero")
  int stockNumberDifferent;

  @NotNull(message = "Inventory ID is required")
  Long inventoryId;

  @NotNull(message = "Product ID is required")
  Long productId;
}
