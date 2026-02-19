package com.forexconverter.service;

import com.forexconverter.Currency;
import com.forexconverter.provider.RateProvider;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;

@Service
public class ConversionService {
  private final RateProvider rateProvider;

  public ConversionService(RateProvider rateProvider) {
    this.rateProvider = rateProvider;
  }

  public BigDecimal convert(Currency from, Currency to, BigDecimal amount) {
    if (from == to) {
      return amount != null ? amount : BigDecimal.ONE;
    }
    BigDecimal rate = rateProvider.getRate(from, to);
    if (amount == null) {
      return rate;
    }
    return rate.multiply(amount);
  }
}
