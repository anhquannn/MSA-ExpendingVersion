package com.market.MSA.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.market.MSA.exceptions.AppException;
import com.market.MSA.exceptions.ErrorCode;
import com.market.MSA.mappers.UserMapper;
import com.market.MSA.models.Role;
import com.market.MSA.models.User;
import com.market.MSA.repositories.RoleRepository;
import com.market.MSA.repositories.UserRepository;
import com.market.MSA.requests.UpdateUserRequest;
import com.market.MSA.requests.UserRequest;
import com.market.MSA.responses.GoogleUser;
import com.market.MSA.responses.UserResponse;
import jakarta.mail.MessagingException;
import java.security.SecureRandom;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class UserService {
  static final String CHARACTERS =
      "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@#$%^&*()";

  @Autowired UserRepository userRepository;

  UserMapper userMapper;
  RoleRepository roleRepository;
  EmailService emailService;
  AuthenticationService authenticationService;

  @Autowired PasswordEncoder passwordEncoder;

  public String registerUser(UserRequest request) {
    if (!emailService.verifyEmail(request.getEmail())) {
      throw new AppException(ErrorCode.INVALID_EMAIL);
    }
    if (userRepository.findByEmail(request.getEmail()).isPresent()) {
      throw new AppException(ErrorCode.USER_EXISTED);
    }
    User user = userMapper.toUser(request);
    Role customerRole =
        roleRepository
            .findByName("CUSTOMER")
            .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));
    user.setPassword(passwordEncoder.encode(request.getPassword()));
    user.setRoles(Set.of(customerRole));
    userRepository.save(user);
    return request.getEmail();
  }

  public String login(String email, String password) throws MessagingException {
    User user =
        userRepository
            .findByEmail(email)
            .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

    if (!passwordEncoder.matches(password, user.getPassword())) {
      throw new AppException(ErrorCode.INVALID_CREDENTIALS);
    }
    return emailService.generateAndSendOTP(email);
  }

  public String verifyOtp(String otp) {
    String email = emailService.validateOTP(otp);
    User user =
        userRepository
            .findByEmail(email)
            .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
    return authenticationService.generateToken(user);
  }

  public String resetPassword(String email) throws MessagingException {
    return emailService.generateAndSendOTP(email);
  }

  public String loginWithGoogle(String accessToken) {
    // Gọi API Google để lấy thông tin người dùng
    RestTemplate restTemplate = new RestTemplate();
    String googleUrl = "https://www.googleapis.com/oauth2/v2/userinfo?access_token=" + accessToken;

    ResponseEntity<String> response = restTemplate.getForEntity(googleUrl, String.class);

    if (response.getStatusCode() != HttpStatus.OK) {
      throw new AppException(ErrorCode.INVALID_GOOGLE_TOKEN);
    }

    try {
      ObjectMapper objectMapper = new ObjectMapper();
      GoogleUser gUser = objectMapper.readValue(response.getBody(), GoogleUser.class);

      if (gUser.getEmail() == null || gUser.getEmail().isEmpty()) {
        throw new AppException(ErrorCode.INVALID_GOOGLE_TOKEN);
      }

      // Kiểm tra xem người dùng đã tồn tại chưa
      Role customerRole =
          roleRepository
              .findByName("CUSTOMER")
              .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));
      User user =
          userRepository
              .findByEmail(gUser.getEmail())
              .orElseGet(
                  () -> {
                    // Tạo mật khẩu ngẫu nhiên
                    String randomPassword = generateRandomPassword();
                    String hashedPassword = passwordEncoder.encode(randomPassword);

                    // Tạo tài khoản mới
                    User newUser =
                        User.builder()
                            .fullName(gUser.getName())
                            .email(gUser.getEmail())
                            .password(hashedPassword)
                            .roles(Set.of(customerRole))
                            .googleId(gUser.getId())
                            .build();

                    User savedUser = userRepository.save(newUser);

                    // Gửi email thông báo mật khẩu
                    try {
                      emailService.sendEmail(
                          savedUser.getEmail(),
                          "Your Account Password",
                          "Your password is: " + randomPassword);
                    } catch (MessagingException e) {
                      // TODO Auto-generated catch block
                      e.printStackTrace();
                    }

                    return savedUser;
                  });

      // Tạo JWT token
      return authenticationService.generateToken(user);

    } catch (JsonProcessingException e) {
      throw new AppException(ErrorCode.PARSE_GOOGLE_RESPONSE_ERROR);
    }
  }

  public UserResponse createUser(UserRequest request) {
    User user = userMapper.toUser(request);
    user.setPassword(passwordEncoder.encode(request.getPassword()));

    try {
      user = userRepository.save(user);
    } catch (DataIntegrityViolationException e) {
      throw new AppException(ErrorCode.USER_EXISTED);
    }

    return userMapper.toUserResponse(user);
  }

  public UserResponse getMyInfo() {
    var context = SecurityContextHolder.getContext();
    String email = context.getAuthentication().getName();

    User user =
        userRepository
            .findByEmail(email)
            .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
    return userMapper.toUserResponse(user);
  }

  @PreAuthorize("hasRole('ADMIN')")
  public List<UserResponse> getUsers() {
    log.info("In method get Users");
    List<User> users = userRepository.findAll();
    return users.stream().map(userMapper::toUserResponse).toList();
  }

  private User getUserEntityByID(long userId) {
    return userRepository
        .findById(userId)
        .orElseThrow(() -> new RuntimeException("User not found"));
  }

  @PostAuthorize("returnObject.username == authentication.name")
  public UserResponse getUserByID(long userId) {
    log.info("In method get user by ID");
    User user = getUserEntityByID(userId);
    return userMapper.toUserResponse(user);
  }

  public UserResponse getUserByEmail(String email) {
    Optional<User> user = userRepository.findByEmail(email);
    return user.map(UserResponse::fromUser).orElse(null);
  }

  public UserResponse getUserByGoogleID(String googleID) {
    Optional<User> user = userRepository.findByGoogleId(googleID);
    return user.map(UserResponse::fromUser).orElse(null);
  }

  public String generateAndSetRandomPasswordByEmail(String email) {
    Optional<User> userOpt = userRepository.findByEmail(email);
    if (userOpt.isEmpty()) {
      return "User not found";
    }

    User user = userOpt.get();
    String newPassword = generateRandomPassword();
    String hashedPassword = passwordEncoder.encode(newPassword);

    user.setPassword(hashedPassword);
    userRepository.save(user);

    try {
      emailService.sendEmail(email, "Your New Password", "Your new password is: " + newPassword);
    } catch (MessagingException e) {
      e.printStackTrace();
    }
    return "Password sent via email and updated successfully";
  }

  public UserResponse updateUser(long userId, UpdateUserRequest request) {
    User user = getUserEntityByID(userId);

    userMapper.updateUser(user, request);
    user.setPassword(passwordEncoder.encode(request.getPassword()));

    var roles = roleRepository.findAllById(request.getRoles());
    user.setRoles(new HashSet<>(roles));

    user = userRepository.save(user);
    return userMapper.toUserResponse(user);
  }

  public void deleteUser(long userId) {
    userRepository.deleteById(userId);
  }

  static String generateRandomPassword() {
    SecureRandom random = new SecureRandom();
    StringBuilder password = new StringBuilder(10);

    for (int i = 0; i < 10; i++) {
      password.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
    }

    return password.toString();
  }
}
