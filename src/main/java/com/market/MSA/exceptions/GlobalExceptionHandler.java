package com.market.MSA.exceptions;

import com.market.MSA.responses.ApiResponse;
import jakarta.validation.ConstraintViolation;
import java.util.Map;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
  private static final String MIN_ATTRIBUTE = "min";
  private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(value = AppException.class)
  public ResponseEntity<ApiResponse<Object>> handleAppException(AppException ex) {
    logger.error("AppException: {}", ex.getMessage(), ex);

    ErrorCode errorCode = ex.getErrorCode();
    ApiResponse<Object> apiResponse =
        new ApiResponse<>(errorCode.getCode(), errorCode.getMessage(), null);
    return ResponseEntity.status(errorCode.getStatusCode()).body(apiResponse);
  }

  @SuppressWarnings("unchecked")
  @ExceptionHandler(value = MethodArgumentNotValidException.class)
  public ResponseEntity<ApiResponse<Object>> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException ex) {
    logger.error("Validation error: {}", ex.getMessage(), ex);

    String enumKey =
        ex.getFieldError() != null ? ex.getFieldError().getDefaultMessage() : "UNKNOWN_ERROR";
    ErrorCode errorCode = ErrorCode.INVALID_TOKEN;
    Map<String, Object> attributes = null;

    try {
      errorCode = ErrorCode.valueOf(enumKey);
      var constraintViolation =
          ex.getBindingResult().getAllErrors().getFirst().unwrap(ConstraintViolation.class);
      attributes = constraintViolation.getConstraintDescriptor().getAttributes();
      log.info(attributes.toString());
    } catch (IllegalArgumentException e) {
      errorCode = ErrorCode.UNCATEGORIZED_EXCEPTION;
    }

    ApiResponse<Object> apiResponse =
        new ApiResponse<>(
            errorCode.getCode(),
            Objects.nonNull(attributes)
                ? mapAttribute(errorCode.getMessage(), attributes)
                : errorCode.getMessage(),
            null);
    return ResponseEntity.badRequest().body(apiResponse);
  }

  @ExceptionHandler(value = AccessDeniedException.class)
  public ResponseEntity<ApiResponse<Object>> handlingAccessDeniedException(
      AccessDeniedException ex) {
    ErrorCode errorCode = ErrorCode.UNAUTHORIZED;

    return ResponseEntity.status(errorCode.getStatusCode())
        .body(
            ApiResponse.<Object>builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build());
  }

  @ExceptionHandler(value = RuntimeException.class)
  public ResponseEntity<ApiResponse<Object>> handleRuntimeException(RuntimeException ex) {
    logger.error("Unhandled RuntimeException: {}", ex.getMessage(), ex);

    ApiResponse<Object> apiResponse =
        new ApiResponse<>(
            ErrorCode.UNCATEGORIZED_EXCEPTION.getCode(),
            ErrorCode.UNCATEGORIZED_EXCEPTION.getMessage(),
            null);
    return ResponseEntity.badRequest().body(apiResponse);
  }

  private String mapAttribute(String message, Map<String, Object> attributes) {
    String minValue = String.valueOf(attributes.get(MIN_ATTRIBUTE));

    return message.replace("{" + MIN_ATTRIBUTE + "}", minValue);
  }
}
