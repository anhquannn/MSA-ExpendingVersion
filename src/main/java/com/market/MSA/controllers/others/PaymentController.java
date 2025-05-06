package com.market.MSA.controllers.others;

import com.market.MSA.constants.ApiMessage;
import com.market.MSA.requests.others.PaymentRequest;
import com.market.MSA.responses.others.ApiResponse;
import com.market.MSA.responses.others.PaymentResponse;
import com.market.MSA.services.others.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
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
import org.springframework.web.bind.annotation.RequestParam;
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
        .message(ApiMessage.PAYMENT_CREATED.getMessage())
        .build();
  }

  @PutMapping("/{id}")
  ApiResponse<PaymentResponse> updatePayment(
      @PathVariable Long id, @RequestBody @Valid PaymentRequest request) {
    return ApiResponse.<PaymentResponse>builder()
        .result(paymentService.updatePayment(id, request))
        .message(ApiMessage.PAYMENT_UPDATED.getMessage())
        .build();
  }

  @DeleteMapping("/{id}")
  ApiResponse<Boolean> deletePayment(@PathVariable Long id) {
    Boolean result = paymentService.deletePayment(id);
    return ApiResponse.<Boolean>builder()
        .result(result)
        .message(ApiMessage.PAYMENT_DELETED.getMessage())
        .build();
  }

  @GetMapping("/{id}")
  ApiResponse<PaymentResponse> getPaymentById(@PathVariable Long id) {
    return ApiResponse.<PaymentResponse>builder()
        .result(paymentService.getPaymentById(id))
        .message(ApiMessage.PAYMENT_RETRIEVED.getMessage())
        .build();
  }

  @GetMapping
  ApiResponse<List<PaymentResponse>> getAllPayments() {
    return ApiResponse.<List<PaymentResponse>>builder()
        .result(paymentService.getAllPayments())
        .message(ApiMessage.ALL_PAYMENTS_RETRIEVED.getMessage())
        .build();
  }

  @PostMapping("/vnpay/{orderId}")
  ApiResponse<String> createVNPayPaymentUrl(
      @PathVariable Long orderId, HttpServletRequest request) {
    return ApiResponse.<String>builder()
        .result(paymentService.createVNPayPaymentUrl(orderId, request))
        .message(ApiMessage.VNPAY_PAYMENT_URL_CREATED.getMessage())
        .build();
  }

  @PostMapping("/vnpay/callback")
  ApiResponse<PaymentResponse> handleVNPayCallback(@RequestParam Map<String, String> params) {
    return ApiResponse.<PaymentResponse>builder()
        .result(paymentService.handleVNPayCallback(params))
        .message(ApiMessage.VNPAY_CALLBACK_HANDLED.getMessage())
        .build();
  }
}
