package com.forexconverter.conversion;

import com.forexconverter.rate.Provider;
import java.math.BigDecimal;
import java.util.Currency;
import org.springframework.stereotype.Service;

@Service
public class ConversionService {
  private final Provider provider;

  public ConversionService(Provider provider) {
    this.provider = provider;
  }

  public BigDecimal convert(Currency from, Currency to, BigDecimal amount) {
    if (from.equals(to)) {
      return amount != null ? amount : BigDecimal.ONE;
    }
    BigDecimal rate = provider.getRate(from, to);
    if (amount == null) {
      return rate;
    }
    return rate.multiply(amount);
  }
}
