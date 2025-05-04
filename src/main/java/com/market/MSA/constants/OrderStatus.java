package com.market.MSA.constants;

import lombok.Getter;

@Getter
public enum OrderStatus {
  ORDER_STATUS_1("pending"),
  ORDER_STATUS_2("paying"),
  ORDER_STATUS_3("paid"),
  ORDER_STATUS_4("delivering"),
  ORDER_STATUS_5("shipped"),
  ORDER_STATUS_6("canceling"),
  ORDER_STATUS_7("cancel"),
  ORDER_STATUS_8("success"),

  ORDER_STATUS_9("failed"),
  ;

  private final String status;

  OrderStatus(String status) {
    this.status = status;
  }
}
