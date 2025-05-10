package com.market.MSA.constants;

import lombok.Getter;

@Getter
public enum RewardPointTransactionType {
  EARN("earn"),
  REDEEM("redeem"),
  ADJUST("adjust"),
  ;

  private final String value;

  RewardPointTransactionType(String value) {
    this.value = value;
  }
}
