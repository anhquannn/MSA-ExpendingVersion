package com.market.MSA.services.others;

import com.market.MSA.exceptions.AppException;
import com.market.MSA.exceptions.ErrorCode;
import com.market.MSA.models.others.OtpData;
import com.market.MSA.responses.user.ZeroBounceResponse;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class EmailService {

  final JavaMailSender mailSender;
  private final ConcurrentHashMap<String, OtpData> otpStore = new ConcurrentHashMap<>();
  private final ConcurrentHashMap<String, String> passwordStore = new ConcurrentHashMap<>();
  private static final long OTP_VALIDITY_SECONDS = 30;

  @Value("${spring.mail.username}")
  private String fromEmail;

  @Value("${zero-bounce.api.key}")
  private String zeroBounceApiKey;

  public EmailService(JavaMailSender mailSender) {
    this.mailSender = mailSender;
  }

  // Gửi email bất đồng bộ
  @Async
  public CompletableFuture<Void> sendEmail(String to, String subject, String body) {
    try {
      MimeMessage message = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message, true);
      helper.setFrom(fromEmail);
      helper.setTo(to);
      helper.setSubject(subject);
      helper.setText(body, true);
      mailSender.send(message);
      return CompletableFuture.completedFuture(null);
    } catch (MessagingException e) {
      return CompletableFuture.failedFuture(e);
    }
  }

  // Sinh OTP và gửi email
  public String generateAndSendOTP(String email) {
    String otp = generateOTP();
    Instant expiryTime = Instant.now().plusSeconds(OTP_VALIDITY_SECONDS);
    otpStore.put(otp, new OtpData(email, expiryTime));
    sendEmail(
        email,
        "Mã OTP của bạn",
        String.format(
            "Mã OTP của bạn là: %s\nMã có hiệu lực trong %d giây.", otp, OTP_VALIDITY_SECONDS));
    return otp;
  }

  // Gửi lại OTP
  public String resendOTP(String email) {
    // Xóa OTP cũ nếu có
    otpStore
        .entrySet()
        .removeIf(
            entry ->
                entry.getValue().getEmail().equals(email)
                    && entry.getValue().getExpiryTime().isAfter(Instant.now()));
    return generateAndSendOTP(email);
  }

  // Kiểm tra OTP hợp lệ
  public String validateOTP(String otp) {
    otp = otp.trim();

    OtpData otpData = otpStore.get(otp);

    if (otpData == null) {
      throw new AppException(ErrorCode.INVALID_OTP);
    }

    if (otpData.getExpiryTime().isBefore(Instant.now())) {
      otpStore.remove(otp);
      throw new AppException(ErrorCode.INVALID_OTP);
    }

    String email = otpData.getEmail();
    otpStore.remove(otp);
    return email;
  }

  // Sinh mật khẩu tạm thời và gửi email
  public String generateAndSendPassword(String email) {
    String password = generateRandomPassword();
    sendEmail(email, "Your Temporary Password", "Your temporary password is: " + password);
    passwordStore.put(email, password);
    return password;
  }

  // Xác minh email qua ZeroBounce
  public boolean verifyEmail(String email) {
    String url =
        String.format(
            "https://api.zerobounce.net/v2/validate?api_key=%s&email=%s", zeroBounceApiKey, email);
    RestTemplate restTemplate = new RestTemplate();
    ZeroBounceResponse response = restTemplate.getForObject(url, ZeroBounceResponse.class);
    return response != null && "valid".equals(response.getStatus());
  }

  // Tạo OTP ngẫu nhiên
  private String generateOTP() {
    SecureRandom secureRandom = new SecureRandom();
    int otp = 100000 + secureRandom.nextInt(900000);
    return String.valueOf(otp);
  }

  // Tạo mật khẩu ngẫu nhiên
  private String generateRandomPassword() {
    return Long.toHexString(Double.doubleToLongBits(Math.random()));
  }
}
