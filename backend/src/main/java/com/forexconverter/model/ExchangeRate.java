package com.forexconverter.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;

public record ExchangeRate(
    Currency sourceCurrency, Currency targetCurrency, BigDecimal rate, LocalDate date) {
  public ExchangeRate {
    if (rate == null || rate.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("Rate must be positive");
    }
    if (rate.compareTo(new BigDecimal("10000")) > 0) {
      throw new IllegalArgumentException("Rate exceeds maximum allowed value");
    }
    if (rate.compareTo(new BigDecimal("0.0001")) < 0) {
      throw new IllegalArgumentException("Rate is below minimum allowed value");
    }
  }
}
