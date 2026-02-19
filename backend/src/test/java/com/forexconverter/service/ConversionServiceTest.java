package com.forexconverter.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.forexconverter.Currency;
import com.forexconverter.provider.RateProvider;
import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
class ConversionServiceTest {

  @DisplayName("ConversionService should multiply amount by rate")
  @Test
  void shouldConvertWithAmount() {
    RateProvider mockProvider = mock(RateProvider.class);
    when(mockProvider.getRate(Currency.USD, Currency.EUR)).thenReturn(new BigDecimal("0.9250"));
    ConversionService service = new ConversionService(mockProvider);

    BigDecimal result = service.convert(Currency.USD, Currency.EUR, new BigDecimal("100"));

    assertThat(result).isEqualByComparingTo(new BigDecimal("92.50"));
  }

  @DisplayName("ConversionService should return rate only when amount is null")
  @Test
  void shouldReturnRateOnly() {
    RateProvider mockProvider = mock(RateProvider.class);
    when(mockProvider.getRate(Currency.USD, Currency.EUR)).thenReturn(new BigDecimal("0.9250"));
    ConversionService service = new ConversionService(mockProvider);

    BigDecimal result = service.convert(Currency.USD, Currency.EUR, null);

    assertThat(result).isEqualByComparingTo(new BigDecimal("0.9250"));
  }

  @DisplayName("ConversionService should return amount for same currency with amount")
  @Test
  void shouldReturnAmountForSameCurrencyWithAmount() {
    RateProvider mockProvider = mock(RateProvider.class);
    ConversionService service = new ConversionService(mockProvider);

    BigDecimal result = service.convert(Currency.USD, Currency.USD, new BigDecimal("100"));

    assertThat(result).isEqualByComparingTo(new BigDecimal("100"));
    verifyNoInteractions(mockProvider);
  }

  @DisplayName("ConversionService should return 1 for same currency without amount")
  @Test
  void shouldReturnOneForSameCurrencyWithoutAmount() {
    RateProvider mockProvider = mock(RateProvider.class);
    ConversionService service = new ConversionService(mockProvider);

    BigDecimal result = service.convert(Currency.USD, Currency.USD, null);

    assertThat(result).isEqualByComparingTo(BigDecimal.ONE);
    verifyNoInteractions(mockProvider);
  }
}
