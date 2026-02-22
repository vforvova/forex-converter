package com.forexconverter.rate;

import java.math.BigDecimal;
import java.util.Currency;

public interface Provider {
  BigDecimal getRate(Currency from, Currency to);
}
