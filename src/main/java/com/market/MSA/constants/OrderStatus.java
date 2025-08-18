package com.market.MSA.constants;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

@Getter
public enum OrderStatus {
  PENDING,
  PAYING,
  PAID,
  DELIVERING,
  SHIPPED,
  CANCELLING,
  CANCELLED,
  COMPLETED,
  RETURNING,
  RETURNED,
  FAILED;

  public static boolean isValidStatus(String status) {
    if (status == null) {
      return false;
    }
    for (OrderStatus os : values()) {
      if (os.name().equalsIgnoreCase(status)) {
        return true;
      }
    }
    return false;
  }

  @JsonCreator
  public static OrderStatus from(String status) {
    if (!isValidStatus(status)) {
      throw new IllegalArgumentException("Invalid order status: " + status);
    }
    return OrderStatus.valueOf(status.toUpperCase());
  }
}
