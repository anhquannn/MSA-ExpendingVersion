package com.market.MSA.constants;

import lombok.Getter;

@Getter
public enum CartStatus {
  CART_STATUS_1("active"),
  ;

  private final String status;

  CartStatus(String status) {
    this.status = status;
  }
}
