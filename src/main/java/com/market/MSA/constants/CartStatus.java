package com.market.MSA.constants;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

@Getter
public enum CartStatus {
  ACTIVE,
  INACTIVE;

  @JsonCreator
  public static CartStatus from(String value) {
    if (value == null) {
      return null;
    }
    for (CartStatus cs : values()) {
      if (cs.name().equalsIgnoreCase(value)) {
        return cs;
      }
    }
    throw new IllegalArgumentException("Invalid CartStatus: " + value);
  }
}
