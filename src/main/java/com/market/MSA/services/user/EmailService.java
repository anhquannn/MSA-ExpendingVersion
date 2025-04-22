package com.market.MSA.services.user;

import com.market.MSA.responses.user.ZeroBounceResponse;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.security.SecureRandom;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class EmailService {

  final JavaMailSender mailSender;
  final ConcurrentHashMap<String, String> otpStore = new ConcurrentHashMap<>();
  final ConcurrentHashMap<String, String> passwordStore = new ConcurrentHashMap<>();

  @Value("${spring.mail.username}")
  private String fromEmail;

  @Value("${zero-bounce.api.key}")
  private String zeroBounceApiKey;

  public EmailService(JavaMailSender mailSender) {
    this.mailSender = mailSender;
  }

  // Gửi email
  public void sendEmail(String to, String subject, String body) throws MessagingException {
    MimeMessage message = mailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(message, true);
    helper.setFrom(fromEmail);
    helper.setTo(to);
    helper.setSubject(subject);
    helper.setText(body);
    mailSender.send(message);
  }

  // Sinh OTP và gửi email
  public String generateAndSendOTP(String email) throws MessagingException {
    String otp = generateOTP();
    sendEmail(email, "Mã OTP của bạn", "Mã OTP của bạn là: " + otp);
    otpStore.put(otp, email);
    return otp;
  }

  // Kiểm tra OTP hợp lệ
  public String validateOTP(String otp) {
    otp = otp.trim();
    log.info("Received OTP: '{}'", otp);
    log.info("Current OTP Store: {}", otpStore);

    if (!otpStore.containsKey(otp)) {
      log.error("OTP not found in store! Received: '{}', Store: {}", otp, otpStore);
      throw new IllegalArgumentException("Invalid OTP");
    }

    String email = otpStore.remove(otp);
    log.info("OTP verified for email: {}", email);
    return email;
  }

  // Sinh mật khẩu tạm thời và gửi email
  public String generateAndSendPassword(String email) throws MessagingException {
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
