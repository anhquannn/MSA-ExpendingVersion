package com.market.MSA.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

  @Bean
  public WebMvcConfigurer corsConfigurer() {
    return new WebMvcConfigurer() {
      @Override
      public void addCorsMappings(CorsRegistry registry) {
        registry
            .addMapping("/**") // Áp dụng cấu hình CORS cho TẤT CẢ các endpoint
            .allowedOrigins(
                "http://localhost:3000",
                "http://127.0.0.1:3000") // <-- RẤT QUAN TRỌNG: Thay bằng ORIGIN CỦA FRONTEND CỦA
            // BẠN
            .allowedMethods(
                "GET", "POST", "PUT", "DELETE", "OPTIONS") // Cho phép các phương thức HTTP này
            .allowedHeaders("*") // Cho phép tất cả các header (bao gồm Authorization)
            .allowCredentials(true); // Cho phép gửi cookies, header xác thực (Authorization)
      }
    };
  }
}
