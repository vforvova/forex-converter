package com.forexconverter.provider;

import com.forexconverter.Currency;
import java.math.BigDecimal;

public interface RateProvider {
  BigDecimal getRate(Currency from, Currency to);
}
