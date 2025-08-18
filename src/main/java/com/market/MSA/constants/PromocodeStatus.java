package com.market.MSA.constants;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

@Getter
public enum PromocodeStatus {
  ACTIVE,
  EXPIRED,
  INACTIVE;

  @JsonCreator
  public static PromocodeStatus from(String value) {
    if (value == null) {
      return null;
    }
    for (PromocodeStatus ps : values()) {
      if (ps.name().equalsIgnoreCase(value)) {
        return ps;
      }
    }
    throw new IllegalArgumentException("Invalid PromocodeStatus: " + value);
  }
}
