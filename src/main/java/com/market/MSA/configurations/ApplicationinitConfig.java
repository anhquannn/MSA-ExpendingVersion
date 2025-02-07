package com.market.MSA.configurations;

import com.market.MSA.exceptions.AppException;
import com.market.MSA.exceptions.ErrorCode;
import com.market.MSA.models.User;
import com.market.MSA.repositories.RoleRepository;
import com.market.MSA.repositories.UserRepository;
import java.util.Set;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ApplicationinitConfig {
  PasswordEncoder passwordEncoder;

  @Bean
  ApplicationRunner applicationRunner(
      UserRepository userRepository, RoleRepository roleRepository) {
    return args -> {
      var adminRole =
          roleRepository
              .findById((long) 1)
              .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));
      if (userRepository.findByEmail("admin").isEmpty()) {
        User user =
            User.builder()
                .email("admin")
                .password(passwordEncoder.encode("admin"))
                .roles(Set.of(adminRole))
                .build();

        userRepository.save(user);
        log.warn("admin user has been created!");
      }
    };
  }
}
