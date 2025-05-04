package com.market.MSA.services.others;

import com.market.MSA.constants.OrderStatus;
import com.market.MSA.exceptions.AppException;
import com.market.MSA.exceptions.ErrorCode;
import com.market.MSA.mappers.others.PaymentMapper;
import com.market.MSA.models.order.Order;
import com.market.MSA.models.others.Payment;
import com.market.MSA.repositories.order.OrderRepository;
import com.market.MSA.repositories.others.PaymentRepository;
import com.market.MSA.repositories.user.UserRepository;
import com.market.MSA.requests.others.PaymentRequest;
import com.market.MSA.responses.others.PaymentResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentService {
  final EntityFinderService entityFinderService;
  final PaymentRepository paymentRepository;
  final UserRepository userRepository;
  final OrderRepository orderRepository;
  final PaymentMapper paymentMapper;

  @NonFinal
  @Value("${vnpay.tmnCode}")
  protected String vnp_TmnCode;

  @NonFinal
  @Value("${vnpay.hashSecret}")
  protected String vnp_HashSecret;

  @NonFinal
  @Value("${vnpay.url}")
  protected String vnp_Url;

  @NonFinal
  @Value("${vnpay.returnUrl}")
  protected String vnp_ReturnUrl;

  @NonFinal
  @Value("${vnpay.apiUrl}")
  protected String vnp_apiUrl;

  @Transactional
  public PaymentResponse createPayment(PaymentRequest request) {
    Payment payment = paymentMapper.toPayment(request);
    payment.setUser(
        entityFinderService.findByIdOrThrow(
            userRepository, request.getUserId(), ErrorCode.USER_NOT_EXISTED));
    payment.setOrder(
        entityFinderService.findByIdOrThrow(
            orderRepository, request.getOrderId(), ErrorCode.ORDER_NOT_FOUND));

    return paymentMapper.toPaymentResponse(paymentRepository.save(payment));
  }

  @Transactional
  public PaymentResponse updatePayment(Long id, PaymentRequest request) {
    Payment payment =
        paymentRepository
            .findById(id)
            .orElseThrow(() -> new AppException(ErrorCode.PAYMENT_NOT_FOUND));
    payment.setUser(
        entityFinderService.findByIdOrThrow(
            userRepository, request.getUserId(), ErrorCode.USER_NOT_EXISTED));
    payment.setOrder(
        entityFinderService.findByIdOrThrow(
            orderRepository, request.getOrderId(), ErrorCode.ORDER_NOT_FOUND));

    paymentMapper.updatePaymentFromRequest(request, payment);
    return paymentMapper.toPaymentResponse(paymentRepository.save(payment));
  }

  @Transactional
  public boolean deletePayment(Long id) {
    if (!paymentRepository.existsById(id)) {
      throw new AppException(ErrorCode.PAYMENT_NOT_FOUND);
    }
    paymentRepository.deleteById(id);
    return true;
  }

  @Transactional(readOnly = true)
  public PaymentResponse getPaymentById(Long id) {
    Payment payment =
        paymentRepository
            .findById(id)
            .orElseThrow(() -> new AppException(ErrorCode.PAYMENT_NOT_FOUND));
    return paymentMapper.toPaymentResponse(payment);
  }

  @Transactional(readOnly = true)
  public List<PaymentResponse> getAllPayments() {
    return paymentRepository.findAll().stream()
        .map(paymentMapper::toPaymentResponse)
        .collect(Collectors.toList());
  }

  @Transactional
  public String createVNPayPaymentUrl(Long orderId, HttpServletRequest request) {
    // Get order
    Order order =
        entityFinderService.findByIdOrThrow(orderRepository, orderId, ErrorCode.ORDER_NOT_FOUND);

    // Create payment record
    Payment payment =
        Payment.builder()
            .order(order)
            .user(order.getUser())
            .grandTotal(order.getGrandTotal())
            .paymentMethod("vnpay")
            .status(OrderStatus.ORDER_STATUS_1.getStatus())
            .transactionId(String.valueOf(orderId))
            .paymentDate(new Date().toString())
            .build();
    paymentRepository.save(payment);

    // Create VNPay payment URL
    String vnp_TxnRef = String.valueOf(orderId);
    String vnp_OrderInfo = "Thanh toan don hang: " + orderId;
    String vnp_OrderType = "billpayment";
    String vnp_Amount = String.valueOf((long) (order.getGrandTotal() * 100));
    String vnp_Locale = "vn";
    String vnp_BankCode = "NCB";
    String vnp_IpAddr = getIpAddress(request);
    String vnp_CreateDate = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
    String vnp_ExpireDate =
        new SimpleDateFormat("yyyyMMddHHmmss")
            .format(new Date(System.currentTimeMillis() + 15 * 60 * 1000));

    Map<String, String> vnp_Params = new HashMap<>();
    vnp_Params.put("vnp_Version", "2.1.0");
    vnp_Params.put("vnp_Command", "pay");
    vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
    vnp_Params.put("vnp_Amount", vnp_Amount);
    vnp_Params.put("vnp_CreateDate", vnp_CreateDate);
    vnp_Params.put("vnp_CurrCode", "VND");
    vnp_Params.put("vnp_IpAddr", vnp_IpAddr);
    vnp_Params.put("vnp_Locale", vnp_Locale);
    vnp_Params.put("vnp_OrderInfo", vnp_OrderInfo);
    vnp_Params.put("vnp_OrderType", vnp_OrderType);
    vnp_Params.put("vnp_ReturnUrl", vnp_ReturnUrl);
    vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
    vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);
    vnp_Params.put("vnp_BankCode", vnp_BankCode);

    // Sort params by key
    List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
    Collections.sort(fieldNames);

    // Build hash data and query
    StringBuilder hashData = new StringBuilder();
    StringBuilder query = new StringBuilder();
    boolean first = true;
    for (String fieldName : fieldNames) {
      String fieldValue = vnp_Params.get(fieldName);
      if (fieldValue != null && !fieldValue.isEmpty()) {
        if (first) {
          hashData
              .append(fieldName)
              .append("=")
              .append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
          query
              .append(fieldName)
              .append("=")
              .append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
          first = false;
        } else {
          hashData
              .append("&")
              .append(fieldName)
              .append("=")
              .append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
          query
              .append("&")
              .append(fieldName)
              .append("=")
              .append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
        }
      }
    }

    // Generate secure hash
    String vnp_SecureHash = hmacSHA512(vnp_HashSecret, hashData.toString());
    query.append("&vnp_SecureHash=").append(vnp_SecureHash);

    return vnp_Url + "?" + query;
  }

  @Transactional
  public PaymentResponse handleVNPayCallback(Map<String, String> params) {
    String vnp_TxnRef = params.get("vnp_TxnRef");
    String vnp_ResponseCode = params.get("vnp_ResponseCode");
    String vnp_TransactionNo = params.get("vnp_TransactionNo");
    String vnp_BankCode = params.get("vnp_BankCode");
    String vnp_PayDate = params.get("vnp_PayDate");
    String vnp_Amount = params.get("vnp_Amount");
    String vnp_SecureHash = params.get("vnp_SecureHash");

    // Find payment by transaction ID
    Payment payment =
        paymentRepository
            .findByTransactionId(vnp_TxnRef)
            .orElseThrow(() -> new AppException(ErrorCode.PAYMENT_NOT_FOUND));

    // Update payment status
    if ("00".equals(vnp_ResponseCode)) {
      payment.setStatus(
          OrderStatus.ORDER_STATUS_1.getStatus()); // Keep as PENDING for admin confirmation
    } else {
      payment.setStatus(OrderStatus.ORDER_STATUS_9.getStatus());
    }

    // Update payment details
    payment.setBankCode(vnp_BankCode);
    payment.setBankTranNo(vnp_TransactionNo);
    payment.setPaymentDate(vnp_PayDate);
    payment.setResponseCode(vnp_ResponseCode);
    payment.setUpdateDate(new Date());

    return paymentMapper.toPaymentResponse(paymentRepository.save(payment));
  }

  private String getIpAddress(HttpServletRequest request) {
    String ipAddress = request.getHeader("X-FORWARDED-FOR");
    if (ipAddress == null) {
      ipAddress = request.getRemoteAddr();
    }
    return ipAddress;
  }

  private String hmacSHA512(String key, String data) {
    try {
      javax.crypto.Mac sha512_HMAC = javax.crypto.Mac.getInstance("HmacSHA512");
      javax.crypto.spec.SecretKeySpec secret_key =
          new javax.crypto.spec.SecretKeySpec(key.getBytes(), "HmacSHA512");
      sha512_HMAC.init(secret_key);
      byte[] hash = sha512_HMAC.doFinal(data.getBytes());
      StringBuilder sb = new StringBuilder(2 * hash.length);
      for (byte b : hash) {
        sb.append(String.format("%02x", b & 0xff));
      }
      return sb.toString();
    } catch (Exception e) {
      throw new AppException(ErrorCode.PAYMENT_ERROR);
    }
  }
}
