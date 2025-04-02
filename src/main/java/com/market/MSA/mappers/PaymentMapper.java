package com.market.MSA.mappers;

import com.market.MSA.models.Payment;
import com.market.MSA.requests.PaymentRequest;
import com.market.MSA.responses.PaymentResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
@Component
public interface PaymentMapper {
  Payment toPayment(PaymentRequest request);

  PaymentResponse toPaymentResponse(Payment payment);

  @Mapping(target = "paymentId", ignore = true)
  void updatePaymentFromRequest(PaymentRequest request, @MappingTarget Payment payment);
}
