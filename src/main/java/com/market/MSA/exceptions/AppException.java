package com.market.MSA.exceptions;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Setter
@Getter
public class AppException extends RuntimeException {
  /** */
  private static final long serialVersionUID = 1L;

  private ErrorCode errorCode;

  public AppException(ErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
  }
}
