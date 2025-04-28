package com.market.MSA.constants;

import lombok.Getter;

@Getter
public enum ProductStatus {
  PENDING("pending"),
  APPROVED("approved"),
  REJECTED("rejected");

  private final String value;

  ProductStatus(String value) {
    this.value = value;
  }
}
