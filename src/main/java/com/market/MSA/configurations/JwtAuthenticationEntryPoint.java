package com.market.MSA.configurations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.market.MSA.exceptions.ErrorCode;
import com.market.MSA.responses.ApiResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

  @Override
  public void commence(
      HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException authException)
      throws IOException, ServletException {

    ErrorCode errorCode = ErrorCode.USER_UNAUTHENTICATED;

    System.out.println(
        "Authentication failed: "
            + authException.getMessage()
            + " Request: "
            + request.getRequestURL());

    response.setStatus(errorCode.getStatusCode().value());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);

    ApiResponse<?> apiResponse =
        ApiResponse.builder().code(errorCode.getCode()).message(errorCode.getMessage()).build();

    ObjectMapper objectMapper = new ObjectMapper();
    response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    response.flushBuffer();
  }
}
