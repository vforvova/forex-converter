package com.forexconverter.provider;

import com.forexconverter.Currency;
import com.forexconverter.exception.ConversionException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class ConfigRateProvider implements RateProvider {
  private static final BigDecimal ROUNDING_SCALE = new BigDecimal("4");
  private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;

  private final Map<Currency, Map<Currency, BigDecimal>> rates;

  public ConfigRateProvider() {
    this.rates =
        Map.of(
            Currency.USD,
                Map.of(
                    Currency.EUR, new BigDecimal("0.9250"),
                    Currency.GBP, new BigDecimal("0.7850"),
                    Currency.JPY, new BigDecimal("149.5000")),
            Currency.EUR,
                Map.of(
                    Currency.GBP, new BigDecimal("0.8486"),
                    Currency.JPY, new BigDecimal("161.6200")),
            Currency.GBP, Map.of(Currency.JPY, new BigDecimal("190.4500")));
  }

  @Override
  public BigDecimal getRate(Currency from, Currency to) {
    Map<Currency, BigDecimal> fromRates = rates.get(from);
    if (fromRates == null || !fromRates.containsKey(to)) {
      throw new ConversionException("Rate not found: " + from + " -> " + to);
    }
    return fromRates.get(to).setScale(4, ROUNDING_MODE);
  }
}
