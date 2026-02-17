package com.forexconverter.provider;

import com.forexconverter.Currency;
import com.forexconverter.client.SwopClient;
import com.forexconverter.dto.SwopRateResponse;
import com.forexconverter.exception.RateNotFoundException;
import com.forexconverter.exception.RateProviderException;
import com.forexconverter.model.ExchangeRate;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

@Service
public class SwopRateProvider implements RateProvider {

  private final SwopClient swopClient;

  public SwopRateProvider(SwopClient swopClient) {
    this.swopClient = swopClient;
  }

  @Override
  public BigDecimal getRate(Currency from, Currency to) {
    try {
      SwopRateResponse response = swopClient.fetchRate(from.name(), to.name());
      ExchangeRate exchangeRate = mapToExchangeRate(response);
      return exchangeRate.rate();
    } catch (Exception e) {
      throw wrapException(e);
    }
  }

  private RuntimeException wrapException(Exception e) {
    if (e instanceof HttpClientErrorException.NotFound) {
      return new RateNotFoundException("Rate not found", e);
    }

    if (e instanceof HttpClientErrorException) {
      return new RateProviderException("Failed to fetch rate from provider", e);
    }

    if (e instanceof HttpServerErrorException) {
      return new RateProviderException("Rate provider server error", e);
    }

    if (e instanceof ResourceAccessException) {
      return new RateProviderException("Rate provider unreachable", e);
    }

    if (e instanceof HttpMessageConversionException) {
      return new RateProviderException("Failed to parse provider response", e);
    }

    return new RateProviderException("Unexpected error fetching rate", e);
  }

  private ExchangeRate mapToExchangeRate(SwopRateResponse response) {
    return new ExchangeRate(
        Currency.valueOf(response.baseCurrency()),
        Currency.valueOf(response.quoteCurrency()),
        response.quote(),
        LocalDate.parse(response.date()));
  }
}
