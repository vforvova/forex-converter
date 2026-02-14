package com.forexconverter;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
class CurrencyTest {

  @DisplayName("Currency enum should contain all supported currencies")
  @Test
  void currencyEnumContainsSupportedValues() {
    assertThat(Currency.values())
        .containsExactlyInAnyOrder(Currency.USD, Currency.EUR, Currency.GBP, Currency.JPY);
  }
}
