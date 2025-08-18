package com.market.MSA.constants;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

/** Trạng thái xử lý yêu cầu trả / đổi hàng. */
@Getter
public enum ReturnStatus {
  PENDING,
  APPROVED,
  REJECTED,
  SHIPPED,
  RECEIVED,
  REFUNDED,
  EXCHANGED;

  public static boolean isValidStatus(String status) {
    if (status == null) return false;
    for (ReturnStatus rs : values()) {
      if (rs.name().equalsIgnoreCase(status)) {
        return true;
      }
    }
    return false;
  }

  @JsonCreator
  public static ReturnStatus from(String status) {
    if (!isValidStatus(status)) {
      throw new IllegalArgumentException("Invalid return status: " + status);
    }
    return ReturnStatus.valueOf(status.toUpperCase());
  }
}
