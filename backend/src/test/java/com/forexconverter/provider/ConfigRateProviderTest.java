package com.forexconverter.provider;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.forexconverter.Currency;
import com.forexconverter.exception.ConversionException;
import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
class ConfigRateProviderTest {

  @DisplayName("ConfigRateProvider should return correct rates with 4 decimal precision")
  @Test
  void shouldReturnCorrectRates() {
    ConfigRateProvider provider = new ConfigRateProvider();

    assertThat(provider.getRate(Currency.USD, Currency.EUR))
        .isEqualByComparingTo(new BigDecimal("0.9250"));
    assertThat(provider.getRate(Currency.EUR, Currency.GBP))
        .isEqualByComparingTo(new BigDecimal("0.8486"));
    assertThat(provider.getRate(Currency.USD, Currency.JPY))
        .isEqualByComparingTo(new BigDecimal("149.5000"));
  }

  @DisplayName("ConfigRateProvider should throw when source currency is not supported")
  @Test
  void shouldThrowForUnsupportedSourceCurrency() {
    ConfigRateProvider provider = new ConfigRateProvider();
    assertThatThrownBy(() -> provider.getRate(Currency.JPY, Currency.USD))
        .isInstanceOf(ConversionException.class)
        .hasMessageContaining("Rate not found");
  }

  @DisplayName("ConfigRateProvider should throw when target currency is not supported")
  @Test
  void shouldThrowForUnsupportedTargetCurrency() {
    ConfigRateProvider provider = new ConfigRateProvider();
    assertThatThrownBy(() -> provider.getRate(Currency.EUR, Currency.USD))
        .isInstanceOf(ConversionException.class)
        .hasMessageContaining("Rate not found");
  }
}
