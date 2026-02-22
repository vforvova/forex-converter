package com.forexconverter.rate;

public class ProviderRateNotFoundException extends RuntimeException {
  public ProviderRateNotFoundException(String message) {
    super(message);
  }

  public ProviderRateNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
}
