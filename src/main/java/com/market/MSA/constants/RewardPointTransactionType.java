package com.market.MSA.constants;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

@Getter
public enum RewardPointTransactionType {
  EARN,
  REDEEM,
  ADJUST;

  @JsonCreator
  public static RewardPointTransactionType from(String value) {
    if (value == null) {
      return null;
    }
    for (RewardPointTransactionType rt : values()) {
      if (rt.name().equalsIgnoreCase(value)) {
        return rt;
      }
    }
    throw new IllegalArgumentException("Invalid RewardPointTransactionType: " + value);
  }
}
