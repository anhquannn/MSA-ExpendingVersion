package com.market.MSA.constants;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

@Getter
public enum ProductStatus {
  PENDING,
  APPROVED,
  REJECTED,
  IN_PROGRESS,
  SHIPPED,
  CANCELLED,
  RECEIVED,
  COMPLETED,
  SENT;

  @JsonCreator
  public static ProductStatus from(String value) {
    if (value == null) {
      return null;
    }
    for (ProductStatus ps : values()) {
      if (ps.name().equalsIgnoreCase(value)) {
        return ps;
      }
    }
    throw new IllegalArgumentException("Invalid ProductStatus: " + value);
  }
}
