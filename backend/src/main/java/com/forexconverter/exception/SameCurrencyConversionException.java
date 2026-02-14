package com.forexconverter.exception;

public class SameCurrencyConversionException extends RuntimeException {

  public SameCurrencyConversionException() {
    super("Source and target currency cannot be the same");
  }
}
