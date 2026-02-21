package com.forexconverter.service;

import com.forexconverter.provider.RateProvider;
import java.math.BigDecimal;
import java.util.Currency;
import org.springframework.stereotype.Service;

@Service
public class ConversionService {
  private final RateProvider rateProvider;

  public ConversionService(RateProvider rateProvider) {
    this.rateProvider = rateProvider;
  }

  public BigDecimal convert(Currency from, Currency to, BigDecimal amount) {
    if (from.equals(to)) {
      return amount != null ? amount : BigDecimal.ONE;
    }
    BigDecimal rate = rateProvider.getRate(from, to);
    if (amount == null) {
      return rate;
    }
    return rate.multiply(amount);
  }
}
