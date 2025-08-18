package com.market.MSA.constants;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum ABCClassification {
  A,
  B,
  C;

  @JsonCreator
  public static ABCClassification from(String value) {
    if (value == null) {
      return null;
    }
    for (ABCClassification abc : values()) {
      if (abc.name().equalsIgnoreCase(value)) {
        return abc;
      }
    }
    throw new IllegalArgumentException("Invalid ABCClassification: " + value);
  }
}
