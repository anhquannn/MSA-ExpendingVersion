package com.market.MSA.constants;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

@Getter
public enum Condition {
  EXCELLENT,
  GOOD,
  BAD;

  public static boolean isValidStatus(String status) {
    if (status == null) {
      return false;
    }
    for (Condition c : values()) {
      if (c.name().equalsIgnoreCase(status)) {
        return true;
      }
    }
    return false;
  }

  @JsonCreator
  public static Condition from(String status) {
    if (!isValidStatus(status)) {
      throw new IllegalArgumentException("Invalid condition status: " + status);
    }
    return Condition.valueOf(status.toUpperCase());
  }
}
