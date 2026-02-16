package com.forexconverter.model;

import static org.junit.jupiter.api.Assertions.*;

import com.forexconverter.Currency;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class ExchangeRateTest {

  @Test
  void shouldCreateExchangeRate() {
    ExchangeRate rate =
        new ExchangeRate(
            Currency.USD, Currency.EUR, new BigDecimal("0.9234"), LocalDate.of(2026, 2, 15));

    assertEquals(Currency.USD, rate.sourceCurrency());
    assertEquals(Currency.EUR, rate.targetCurrency());
    assertEquals(new BigDecimal("0.9234"), rate.rate());
    assertEquals(LocalDate.of(2026, 2, 15), rate.date());
  }

  @Test
  void shouldValidatePositiveRate() {
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          new ExchangeRate(Currency.USD, Currency.EUR, BigDecimal.ZERO, LocalDate.now());
        });

    assertThrows(
        IllegalArgumentException.class,
        () -> {
          new ExchangeRate(Currency.USD, Currency.EUR, new BigDecimal("-1.0"), LocalDate.now());
        });
  }

  @Test
  void shouldValidateRateAboveMaximum() {
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          new ExchangeRate(Currency.USD, Currency.EUR, new BigDecimal("10001"), LocalDate.now());
        });
  }

  @Test
  void shouldValidateRateBelowMinimum() {
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          new ExchangeRate(Currency.USD, Currency.EUR, new BigDecimal("0.00001"), LocalDate.now());
        });
  }
}
