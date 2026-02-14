package com.forexconverter.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.forexconverter.Currency;
import com.forexconverter.exception.SameCurrencyConversionException;
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

  @DisplayName("ConversionService should throw when converting same currency")
  @Test
  void shouldThrowForSameCurrency() {
    RateProvider mockProvider = mock(RateProvider.class);
    ConversionService service = new ConversionService(mockProvider);

    assertThatThrownBy(() -> service.convert(Currency.USD, Currency.USD, new BigDecimal("100")))
        .isInstanceOf(SameCurrencyConversionException.class)
        .hasMessageContaining("Source and target currency cannot be the same");
  }
}
