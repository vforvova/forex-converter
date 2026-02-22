package com.forexconverter.rate;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;
import org.junit.jupiter.api.Test;

class RateTest {

  @Test
  void shouldCreateRate() {
    Currency usd = Currency.getInstance("USD");
    Currency eur = Currency.getInstance("EUR");
    Rate rate = new Rate(usd, eur, new BigDecimal("0.9234"), LocalDate.of(2026, 2, 15));

    assertEquals(usd, rate.sourceCurrency());
    assertEquals(eur, rate.targetCurrency());
    assertEquals(new BigDecimal("0.9234"), rate.rate());
    assertEquals(LocalDate.of(2026, 2, 15), rate.date());
  }

  @Test
  void shouldValidatePositiveRate() {
    Currency usd = Currency.getInstance("USD");
    Currency eur = Currency.getInstance("EUR");
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          new Rate(usd, eur, BigDecimal.ZERO, LocalDate.now());
        });

    assertThrows(
        IllegalArgumentException.class,
        () -> {
          new Rate(usd, eur, new BigDecimal("-1.0"), LocalDate.now());
        });
  }

  @Test
  void shouldValidateRateAboveMaximum() {
    Currency usd = Currency.getInstance("USD");
    Currency eur = Currency.getInstance("EUR");
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          new Rate(usd, eur, new BigDecimal("10001"), LocalDate.now());
        });
  }

  @Test
  void shouldValidateRateBelowMinimum() {
    Currency usd = Currency.getInstance("USD");
    Currency eur = Currency.getInstance("EUR");
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          new Rate(usd, eur, new BigDecimal("0.00001"), LocalDate.now());
        });
  }
}
