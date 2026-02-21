package com.forexconverter.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.forexconverter.provider.RateProvider;
import java.math.BigDecimal;
import java.util.Currency;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
class ConversionServiceTest {

  @DisplayName("ConversionService should multiply amount by rate")
  @Test
  void shouldConvertWithAmount() {
    RateProvider mockProvider = mock(RateProvider.class);
    Currency usd = Currency.getInstance("USD");
    Currency eur = Currency.getInstance("EUR");
    when(mockProvider.getRate(usd, eur)).thenReturn(new BigDecimal("0.9250"));
    ConversionService service = new ConversionService(mockProvider);

    BigDecimal result = service.convert(usd, eur, new BigDecimal("100"));

    assertThat(result).isEqualByComparingTo(new BigDecimal("92.50"));
  }

  @DisplayName("ConversionService should return rate only when amount is not provided")
  @Test
  void shouldReturnRateOnly() {
    RateProvider mockProvider = mock(RateProvider.class);
    Currency usd = Currency.getInstance("USD");
    Currency eur = Currency.getInstance("EUR");
    when(mockProvider.getRate(usd, eur)).thenReturn(new BigDecimal("0.9250"));
    ConversionService service = new ConversionService(mockProvider);

    BigDecimal result = service.convert(usd, eur, null);

    assertThat(result).isEqualByComparingTo(new BigDecimal("0.9250"));
  }

  @DisplayName("ConversionService should return amount when provided currencies are the same")
  @Test
  void shouldReturnAmountForSameCurrencyWithAmount() {
    RateProvider mockProvider = mock(RateProvider.class);
    ConversionService service = new ConversionService(mockProvider);
    Currency usd = Currency.getInstance("USD");

    BigDecimal result = service.convert(usd, usd, new BigDecimal("100"));

    assertThat(result).isEqualByComparingTo(new BigDecimal("100"));
    verifyNoInteractions(mockProvider);
  }

  @DisplayName("ConversionService should return 1 when provided currencies are the same and amount isn't specified")
  @Test
  void shouldReturnOneForSameCurrencyWithoutAmount() {
    RateProvider mockProvider = mock(RateProvider.class);
    ConversionService service = new ConversionService(mockProvider);
    Currency usd = Currency.getInstance("USD");

    BigDecimal result = service.convert(usd, usd, null);

    assertThat(result).isEqualByComparingTo(BigDecimal.ONE);
    verifyNoInteractions(mockProvider);
  }
}
