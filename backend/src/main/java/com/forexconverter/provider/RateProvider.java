package com.forexconverter.provider;

import java.math.BigDecimal;
import java.util.Currency;

public interface RateProvider {
  BigDecimal getRate(Currency from, Currency to);
}
