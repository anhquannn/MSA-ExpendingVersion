package com.market.MSA.mappers.others;

import com.market.MSA.models.others.Payment;
import com.market.MSA.requests.others.PaymentRequest;
import com.market.MSA.responses.others.PaymentResponse;
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
