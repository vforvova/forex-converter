package com.forexconverter.exception;

public class RateNotFoundException extends RuntimeException {
  public RateNotFoundException(String message) {
    super(message);
  }

  public RateNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
}
