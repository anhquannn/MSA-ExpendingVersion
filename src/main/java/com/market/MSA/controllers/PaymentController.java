package com.market.MSA.controllers;

import com.market.MSA.requests.PaymentRequest;
import com.market.MSA.responses.ApiResponse;
import com.market.MSA.responses.PaymentResponse;
import com.market.MSA.services.PaymentService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentController {
  PaymentService paymentService;

  @PostMapping
  ApiResponse<PaymentResponse> createPayment(@RequestBody @Valid PaymentRequest request) {
    return ApiResponse.<PaymentResponse>builder()
        .result(paymentService.createPayment(request))
        .build();
  }

  @PutMapping("/{id}")
  ApiResponse<PaymentResponse> updatePayment(
      @PathVariable Long id, @RequestBody @Valid PaymentRequest request) {
    return ApiResponse.<PaymentResponse>builder()
        .result(paymentService.updatePayment(id, request))
        .build();
  }

  @DeleteMapping("/{id}")
  ApiResponse<String> deletePayment(@PathVariable Long id) {
    paymentService.deletePayment(id);
    return ApiResponse.<String>builder().result("Payment deleted successfully").build();
  }

  @GetMapping("/{id}")
  ApiResponse<PaymentResponse> getPaymentById(@PathVariable Long id) {
    return ApiResponse.<PaymentResponse>builder().result(paymentService.getPaymentById(id)).build();
  }

  @GetMapping
  ApiResponse<List<PaymentResponse>> getAllPayments() {
    return ApiResponse.<List<PaymentResponse>>builder()
        .result(paymentService.getAllPayments())
        .build();
  }
}
