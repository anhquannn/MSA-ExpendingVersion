package com.market.MSA.constants;

import lombok.Getter;

@Getter
public enum PromocodeStatus {
  PROMO_CODE_STATUS_1("active"),
  PROMO_CODE_STATUS_2("expired"),
  PROMO_CODE_STATUS_3("unavailable");

  private final String status;

  PromocodeStatus(String status) {
    this.status = status;
  }
}
