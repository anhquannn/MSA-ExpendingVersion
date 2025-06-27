package com.market.MSA.requests.filters;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.market.MSA.constants.OrderStatus;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeliveryInfoFilterRequest {
  Long orderId;
  OrderStatus status;
  String city;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  LocalDateTime fromDate;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  LocalDateTime toDate;

  int page = 0;
  int size = 20;
}
